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
import cn.edu.zjut.service.ItemService;
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

    @Autowired
    private ItemService itemService;

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

        // 判断库存是否已售罄，售罄直接返回下单失败
        if (Boolean.TRUE.equals(this.redisTemplate.hasKey("promo_item_stock_invalid_" + itemId))) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        // 加入库存流水init状态
        String itemStockLogId = this.itemService.initStockLog(itemId, amount);

        // 完成对应下单事务型消息机制
        if (!this.mqProducer.transactionAsyncReduceStock(userModel.getId(), itemId, amount, promoId, itemStockLogId)) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR, "下单失败");
        }

        return CommonReturnType.create(null);
    }
}
