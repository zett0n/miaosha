package cn.edu.zjut.service.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author zett0n
 * @date 2021/8/9 21:16 用户交易模型
 */
@Data
public class OrderModel {
    // 企业级一般用String类型，而不是int
    private String id;

    private Integer userId;

    private Integer itemId;

    private Integer amount;

    // 购买时商品的单价，若promoId非空则表示秒杀商品的单价
    private BigDecimal itemPrice;

    // 总金额，若promoId非空则表示秒杀商品的总金额
    private BigDecimal orderPrice;

    // 若非空则表示以秒杀商品方式下单
    private Integer promoId;
}
