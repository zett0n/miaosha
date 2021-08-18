package cn.edu.zjut.entity;

import lombok.Data;

@Data
public class ItemStockLogInfo {
    private String id;

    private Integer itemId;

    private Integer amount;

    private Integer status;

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }
}