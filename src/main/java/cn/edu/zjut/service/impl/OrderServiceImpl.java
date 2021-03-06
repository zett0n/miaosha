package cn.edu.zjut.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.edu.zjut.dao.OrderInfoMapper;
import cn.edu.zjut.dao.SequenceInfoMapper;
import cn.edu.zjut.entity.OrderInfo;
import cn.edu.zjut.entity.SequenceInfo;
import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.error.EmBusinessError;
import cn.edu.zjut.service.ItemService;
import cn.edu.zjut.service.OrderService;
import cn.edu.zjut.service.UserService;
import cn.edu.zjut.service.model.ItemModel;
import cn.edu.zjut.service.model.OrderModel;
import cn.edu.zjut.service.model.PromoModel;
import cn.edu.zjut.service.model.UserModel;

/**
 * @author zett0n
 * @date 2021/8/9 21:31
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private SequenceInfoMapper sequenceInfoMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer amount, Integer promoId)
        throws BusinessException {
        // 检验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = this.itemService.getItemById(itemId);
        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        }

        UserModel userModel = this.userService.getUserById(userId);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
        }

        if (amount <= 0 || amount > 99) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "购买数量不正确");
        }

        // 检验秒杀活动信息
        PromoModel promoModel = itemModel.getPromoModel();
        if (promoId != null) {
            // 秒杀活动是否存在对应商品
            if (promoId.intValue() != promoModel.getId()) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "秒杀信息不正确");
            }
            // 检验秒杀是否正在进行
            if (promoModel.getStatus() != 2) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "秒杀未进行中");
            }
        }

        // 落单减库存
        boolean result = this.itemService.decreaseStock(itemId, amount);
        if (!result) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        // 订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);

        if (promoId != null) {
            orderModel.setPromoId(promoId);
            orderModel.setItemPrice(promoModel.getPromoItemPrice());
        } else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

        // 生成订单流水号id
        orderModel.setId(generateOrderId());
        OrderInfo orderInfo = convertInfoFromModel(orderModel);
        this.orderInfoMapper.insertSelective(orderInfo);

        // 增加商品销量
        this.itemService.increaseSales(itemId, amount);

        // 返回前端
        return orderModel;
    }

    private OrderInfo convertInfoFromModel(OrderModel orderModel) {
        if (orderModel == null) {
            return null;
        }
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderModel, orderInfo);
        orderInfo.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderInfo.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderInfo;
    }

    // 无论代码是否在事务中，都会开启新事务并在执行完后提交
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private String generateOrderId() {
        // 16位
        // 8位时间（年月日）
        StringBuilder sb = new StringBuilder();

        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        sb.append(nowDate);

        // 中间6位自增序列
        SequenceInfo sequenceInfo = this.sequenceInfoMapper.getSequenceByName("order_info");
        Integer currentValue = sequenceInfo.getCurrentValue();

        sequenceInfo.setCurrentValue(sequenceInfo.getCurrentValue() + sequenceInfo.getStep());
        this.sequenceInfoMapper.updateByPrimaryKeySelective(sequenceInfo);

        String sequenceStr = String.valueOf(currentValue);
        for (int i = 0; i < 6 - sequenceStr.length(); i++) {
            sb.append(0);
        }
        sb.append(sequenceStr);

        // 最后2位分库分表位
        sb.append("00");

        return sb.toString();
    }
}
