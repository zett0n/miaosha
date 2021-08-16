package cn.edu.zjut.controller.vo;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author zett0n
 * @date 2021/8/9 19:54
 */
@Data
public class ItemVO {
    private Integer id;

    private String title;

    private BigDecimal price;

    private Integer stock;

    private String description;

    private Integer sales;

    private String imgUrl;

    private Integer promoStatus;

    private BigDecimal promoPrice;

    private Integer promoId;

    private String startDate;
}
