package cn.edu.zjut.service.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author zett0n
 * @date 2021/8/9 17:31
 */
@Data
public class ItemModel implements Serializable {
    private Integer id;

    @NotBlank(message = "商品名称不能为空")
    private String title;

    @NotNull(message = "商品价格不能为空")
    @Min(value = 0, message = "商品价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "库存不能为0")
    private Integer stock;

    @NotBlank(message = "商品描述不能为空")
    private String description;

    private Integer sales;

    @NotBlank(message = "商品图片不能为空")
    private String imgUrl;

    // 使用聚合模型，如果该对象不为空，则表示该商品有未结束的秒杀活动
    private PromoModel promoModel;
}
