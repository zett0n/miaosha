package cn.edu.zjut.service.model;

import java.math.BigDecimal;

/**
 * @author zett0n
 * @date 2021/8/9 21:16 用户交易模型
 */
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

    public Integer getPromoId() {
        return this.promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return this.itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getItemPrice() {
        return this.itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public BigDecimal getOrderPrice() {
        return this.orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }
}
