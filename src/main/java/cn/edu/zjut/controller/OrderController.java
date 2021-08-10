package cn.edu.zjut.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.error.EmBusinessError;
import cn.edu.zjut.response.CommonReturnType;
import cn.edu.zjut.service.OrderService;
import cn.edu.zjut.service.model.UserModel;

/**
 * @author zett0n
 * @date 2021/8/9 22:46
 */
@RestController
@RequestMapping("/order")
@CrossOrigin(origins = {" * "}, allowedHeaders = "*")
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    // 封装下单请求
    @PostMapping("/create")
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
        @RequestParam(name = "promoId", required = false) Integer promoId,
        @RequestParam(name = "amount") Integer amount) throws BusinessException {

        Boolean isLogin = (Boolean)this.httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (isLogin == null || !isLogin) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        }

        // 获取用户的登录信息
        UserModel userModel = (UserModel)this.httpServletRequest.getSession().getAttribute("LOGIN_USER");
        this.orderService.createOrder(userModel.getId(), itemId, amount, promoId);

        return CommonReturnType.create(null);
    }
}
