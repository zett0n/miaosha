package cn.edu.zjut.controller;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.google.common.util.concurrent.RateLimiter;

import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.error.EmBusinessError;
import cn.edu.zjut.mq.MQProducer;
import cn.edu.zjut.response.CommonReturnType;
import cn.edu.zjut.service.ItemService;
import cn.edu.zjut.service.OrderService;
import cn.edu.zjut.service.PromoService;
import cn.edu.zjut.service.model.UserModel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zett0n
 * @date 2021/8/9 22:46
 */
@RestController
@RequestMapping("/order")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@Slf4j
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQProducer mqProducer;

    @Autowired
    private ItemService itemService;

    @Autowired
    private PromoService promoService;

    private ExecutorService executorService;

    private RateLimiter orderCreateRateLimiter;

    @PostConstruct
    public void initExecutorService() {
        this.executorService = Executors.newFixedThreadPool(20);
        this.orderCreateRateLimiter = RateLimiter.create(200);
    }

    // 封装下单请求
    @PostMapping("/create")
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
        @RequestParam(name = "promoId", required = false) Integer promoId,
        @RequestParam(name = "amount") Integer amount, @RequestParam(name = "loginToken") String loginToken,
        @RequestParam(name = "promoToken", required = false) String promoToken) throws BusinessException {

        if (this.orderCreateRateLimiter.tryAcquire()) {
            throw new BusinessException(EmBusinessError.RATE_LIMIT);
        }

        // [获取用户的登录信息]
        // 基于session
        // Boolean isLogin = (Boolean)this.httpSession.getAttribute("IS_LOGIN");
        // if (isLogin == null || !isLogin) {
        // throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        // }
        // UserModel userModel = (UserModel)this.httpSession.getAttribute("LOGIN_USER");

        // 基于token
        UserModel userModel;
        if (StringUtils.isEmpty(loginToken)
            || null == (userModel = (UserModel)this.redisTemplate.opsForValue().get(loginToken))) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        }

        // 校验秒杀令牌是否正确
        if (promoId != null) {
            String promoTokenInRedis = (String)this.redisTemplate.opsForValue()
                .get("user_id_" + userModel.getId() + "_item_id_" + itemId + "_promo_token_" + promoId);
            if (promoTokenInRedis == null || !StringUtils.equals(promoToken, promoTokenInRedis)) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "秒杀令牌校验失败");
            }
        }

        // this.orderService.createOrder(userModel.getId(), itemId, amount, promoId);

        // 判断库存是否已售罄，售罄直接返回下单失败
        if (Boolean.TRUE.equals(this.redisTemplate.hasKey("promo_item_stock_invalid_" + itemId))) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        // 拥塞窗口为20的等待队列，用于队列化泄洪
        // 同步调用线程池的submit方法
        Future<Object> future = this.executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                // 加入库存流水init状态
                String itemStockLogId = OrderController.this.itemService.initStockLog(itemId, amount);

                // 完成对应下单事务型消息机制
                if (!OrderController.this.mqProducer.transactionAsyncReduceStock(userModel.getId(), itemId, amount,
                    promoId, itemStockLogId)) {
                    throw new BusinessException(EmBusinessError.UNKNOWN_ERROR, "下单失败");
                }
                return null;
            }
        });

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }
        return CommonReturnType.create(null);
    }

    // 生成验证码
    @GetMapping("/generateVerifyCode")
    public void generateVerifyCode(@RequestParam(name = "loginToken") String loginToken, HttpServletResponse response)
        throws BusinessException, IOException {

        // 验证用户登录（基于token）
        UserModel userModel;
        if (StringUtils.isEmpty(loginToken)
            || null == (userModel = (UserModel)this.redisTemplate.opsForValue().get(loginToken))) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登录，不能生成验证码");
        }

        // 生成验证码
        Map<String, Object> map = person.sa.util.VerifyCodeGenerator.generateCodeAndPic();
        ImageIO.write((RenderedImage)map.get("codePic"), "jpeg", response.getOutputStream());
        this.redisTemplate.opsForValue().set("verify_code_" + userModel.getId(), map.get("code"));
        this.redisTemplate.expire("verify_code_" + userModel.getId(), 10, TimeUnit.MINUTES);
        log.debug("id {} 用户的验证码为：{}", userModel.getId(), map.get("code"));
    }

    // 生成秒杀令牌
    @PostMapping("/generatePromoToken")
    public CommonReturnType generatePromoToken(@RequestParam(name = "itemId") Integer itemId,
        @RequestParam(name = "promoId", required = true) Integer promoId,
        @RequestParam(name = "loginToken") String loginToken, @RequestParam(name = "verifyCode") String verifyCode)
        throws BusinessException {

        // 验证用户登录（基于token）
        UserModel userModel;
        if (StringUtils.isEmpty(loginToken)
            || null == (userModel = (UserModel)this.redisTemplate.opsForValue().get(loginToken))) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        }

        // 验证码检验
        String verifyCodeInRedis = (String)this.redisTemplate.opsForValue().get("verify_code_" + userModel.getId());
        if (StringUtils.isEmpty(verifyCodeInRedis) || !verifyCodeInRedis.equalsIgnoreCase(verifyCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "验证码错误");
        }

        // 获取秒杀访问令牌
        String promoToken = this.promoService.generatePromoToken(userModel.getId(), itemId, promoId);
        if (promoToken == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "生成令牌失败");
        }
        return CommonReturnType.create(promoToken);
    }
}