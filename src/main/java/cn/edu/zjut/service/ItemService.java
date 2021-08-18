package cn.edu.zjut.service;

import java.util.List;

import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.service.model.ItemModel;

/**
 * @author zett0n
 * @date 2021/8/9 19:02
 */
public interface ItemService {
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    List<ItemModel> listItem();

    ItemModel getItemById(Integer id);

    boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;

    void increaseSales(Integer itemId, Integer amount) throws BusinessException;

    // itemModel promoModel缓存模型
    ItemModel getItemByIdInCache(Integer id);

    // 异步更新库存
    boolean asyncDecreaseStock(Integer itemId, Integer amount);

    // redis库存回补
    boolean increaseStock(Integer itemId, Integer amount);

    // 初始化库存流水
    String initStockLog(Integer itemId, Integer amount);
}
