package cn.edu.zjut.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.edu.zjut.controller.vo.ItemVO;
import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.response.CommonReturnType;
import cn.edu.zjut.service.ItemService;
import cn.edu.zjut.service.model.ItemModel;

/**
 * @author zett0n
 * @date 2021/8/9 19:53
 */
@RestController
@RequestMapping("/item")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/create")
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
        @RequestParam(name = "description") String description, @RequestParam(name = "price") BigDecimal price,
        @RequestParam(name = "stock") Integer stock, @RequestParam(name = "imgUrl") String imgUrl)
        throws BusinessException {

        // 封装service请求，用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = this.itemService.createItem(itemModel);
        ItemVO itemVO = convertVOFromModel(itemModelForReturn);
        return CommonReturnType.create(itemVO);
    }

    @GetMapping("/get")
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id) {
        ItemModel itemModel = this.itemService.getItemById(id);
        ItemVO itemVO = convertVOFromModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    @GetMapping("/list")
    public CommonReturnType listItem() {
        List<ItemModel> itemModelList = this.itemService.listItem();
        List<ItemVO> list =
            itemModelList.stream().map(itemModel -> convertVOFromModel(itemModel)).collect(Collectors.toList());
        return CommonReturnType.create(list);
    }

    private ItemVO convertVOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);

        if (itemModel.getPromoModel() != null) {
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
            itemVO.setStartDate(
                itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }
}
