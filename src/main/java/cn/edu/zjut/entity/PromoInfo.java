package cn.edu.zjut.entity;

import java.util.Date;

import lombok.Data;

@Data
public class PromoInfo {
    private Integer id;

    private String promoName;

    private Date startDate;

    private Date endDate;

    private Integer itemId;

    private Double promoItemPrice;

    public void setPromoName(String promoName) {
        this.promoName = promoName == null ? null : promoName.trim();
    }
}