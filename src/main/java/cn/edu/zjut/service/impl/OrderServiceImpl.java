package cn.edu.zjut.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.edu.zjut.dao.ItemStockLogInfoMapper;
import cn.edu.zjut.dao.OrderInfoMapper;
import cn.edu.zjut.dao.SequenceInfoMapper;
import cn.edu.zjut.entity.ItemStockLogInfo;
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

    @Autowired
    private ItemStockLogInfoMapper itemStockLogInfoMapper;

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
    public String generateOrderId() {
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

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer amount, Integer promoId,
        String itemStockLogId) throws BusinessException {
        // 1.检验下单状态
        // 下单的商品是否存在
        // ItemModel itemModel = this.itemService.getItemById(itemId);
        ItemModel itemModel = this.itemService.getItemByIdInCache(itemId);
        if (itemModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
        }
        // 用户是否合法
        // UserModel userModel = this.userService.getUserById(userId);
        UserModel userModel = this.userService.getUserByIdInCache(userId);
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不存在");
        }
        // 购买数量是否正确
        if (amount <= 0 || amount > 99) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "购买数量不正确");
        }

        // 2.检验秒杀活动信息
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

        // 3.落单减库存
        boolean result = this.itemService.decreaseStock(itemId, amount);
        if (!result) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        // 4.订单入库
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

        // 5.生成订单流水号id
        orderModel.setId(generateOrderId());
        OrderInfo orderInfo = convertInfoFromModel(orderModel);
        this.orderInfoMapper.insertSelective(orderInfo);

        // 6.增加商品销量
        this.itemService.increaseSales(itemId, amount);

        // 7.异步更新库存
        // TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
        // @Override
        // public void afterCommit() {
        // boolean mqResult = OrderServiceImpl.this.itemService.asyncDecreaseStock(itemId, amount);
        // if (!mqResult) {
        // // mq结果失败，回补redis
        // OrderServiceImpl.this.itemService.increaseStock(itemId, amount);
        // throw new BusinessException(EmBusinessError.MQ_SEND_FAIL);
        // }
        // }
        // });

        // 7.设置库存流水状态为成功
        ItemStockLogInfo itemStockLogInfo = this.itemStockLogInfoMapper.selectByPrimaryKey(itemStockLogId);
        if (itemStockLogInfo == null) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }
        itemStockLogInfo.setStatus(2);
        this.itemStockLogInfoMapper.updateByPrimaryKeySelective(itemStockLogInfo);

        // 测试mq checkLocalTransaction
        // try {
        // Thread.sleep(20000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        // 8.返回前端
        return orderModel;
    }
}
