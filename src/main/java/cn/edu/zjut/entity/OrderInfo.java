package cn.edu.zjut.entity;

import lombok.Data;

@Data
public class OrderInfo {
    private String id;

    private Integer userId;

    private Integer itemId;

    private Integer amount;

    private Double itemPrice;

    private Double orderPrice;

    private Integer promoId;

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }
}