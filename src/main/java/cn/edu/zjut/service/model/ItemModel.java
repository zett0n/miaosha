package cn.edu.zjut.service.model;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zett0n
 * @date 2021/8/9 17:31
 */
public class ItemModel {
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

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return this.stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSales() {
        return this.sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public PromoModel getPromoModel() {
        return this.promoModel;
    }

    public void setPromoModel(PromoModel promoModel) {
        this.promoModel = promoModel;
    }
}
