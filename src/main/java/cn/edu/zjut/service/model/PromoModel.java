package cn.edu.zjut.service.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.joda.time.DateTime;

import lombok.Data;

/**
 * @author zett0n
 * @date 2021/8/10 14:41
 */
@Data
public class PromoModel implements Serializable {
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
}
