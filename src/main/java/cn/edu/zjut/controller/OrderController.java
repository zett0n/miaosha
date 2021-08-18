package cn.edu.zjut.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.error.EmBusinessError;
import cn.edu.zjut.mq.MQProducer;
import cn.edu.zjut.response.CommonReturnType;
import cn.edu.zjut.service.OrderService;
import cn.edu.zjut.service.model.UserModel;

/**
 * @author zett0n
 * @date 2021/8/9 22:46
 */
@RestController
@RequestMapping("/order")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQProducer mqProducer;

    // 封装下单请求
    @PostMapping("/create")
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
        @RequestParam(name = "promoId", required = false) Integer promoId,
        @RequestParam(name = "amount") Integer amount, @RequestParam(name = "token") String token)
        throws BusinessException {

        // [获取用户的登录信息]
        // 基于session
        // Boolean isLogin = (Boolean)this.httpSession.getAttribute("IS_LOGIN");
        // if (isLogin == null || !isLogin) {
        // throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        // }
        // UserModel userModel = (UserModel)this.httpSession.getAttribute("LOGIN_USER");

        // 基于token
        UserModel userModel;
        if (StringUtils.isEmpty(token)
            || null == (userModel = (UserModel)this.redisTemplate.opsForValue().get(token))) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        }

        // this.orderService.createOrder(userModel.getId(), itemId, amount, promoId);
        if (!this.mqProducer.transactionAsyncReduceStock(userModel.getId(), itemId, amount, promoId)) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR, "下单失败");
        }

        return CommonReturnType.create(null);
    }
}
