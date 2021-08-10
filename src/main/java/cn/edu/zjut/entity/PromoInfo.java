package cn.edu.zjut.entity;

import java.util.Date;

public class PromoInfo {
    private Integer id;

    private String promoName;

    private Date startDate;

    private Date endDate;

    private Integer itemId;

    private Double promoItemPrice;

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
        this.promoName = promoName == null ? null : promoName.trim();
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getItemId() {
        return this.itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Double getPromoItemPrice() {
        return this.promoItemPrice;
    }

    public void setPromoItemPrice(Double promoItemPrice) {
        this.promoItemPrice = promoItemPrice;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}