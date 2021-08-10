package cn.edu.zjut.service.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

/**
 * @author zett0n
 * @date 2021/8/10 14:41
 */
public class PromoModel {
    private Integer id;

    // 秒杀活动名称
    private String promoName;

    // 秒杀开始时间
    private DateTime startDate;

    // 秒杀结束时间
    private DateTime endDate;

    // 参与活动的商品
    private Integer itemId;

    // 秒杀价格
    private BigDecimal promoItemPrice;

    // 秒杀活动状态，1是还未开始，2是进行中，3是已结束
    private Integer status;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromoName() {
        return this.promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public DateTime getStartDate() {
        return this.startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public Integer getItemId() {
        return this.itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getPromoItemPrice() {
        return this.promoItemPrice;
    }

    public void setPromoItemPrice(BigDecimal promoItemPrice) {
        this.promoItemPrice = promoItemPrice;
    }

    public DateTime getEndDate() {
        return this.endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
