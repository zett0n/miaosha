package cn.edu.zjut.service;

import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.service.model.OrderModel;

/**
 * @author zett0n
 * @date 2021/8/9 21:30
 */
public interface OrderService {
    OrderModel createOrder(Integer userId, Integer itemId, Integer amount, Integer promoId) throws BusinessException;
}
