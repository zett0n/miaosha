package cn.edu.zjut.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.edu.zjut.dao.ItemInfoMapper;
import cn.edu.zjut.dao.ItemStockMapper;
import cn.edu.zjut.entity.ItemInfo;
import cn.edu.zjut.entity.ItemStock;
import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.error.EmBusinessError;
import cn.edu.zjut.service.ItemService;
import cn.edu.zjut.service.model.ItemModel;
import cn.edu.zjut.validater.ValidationResult;
import cn.edu.zjut.validater.ValidatorImpl;

/**
 * @author zett0n
 * @date 2021/8/9 19:04
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemInfoMapper itemInfoMapper;

    @Autowired
    private ItemStockMapper itemStockMapper;

    private ItemInfo convertInfoFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemInfo itemInfo = new ItemInfo();
        // 不会拷贝类型不同的对象
        BeanUtils.copyProperties(itemModel, itemInfo);
        // double传到json会存在精度问题
        itemInfo.setPrice(itemModel.getPrice().doubleValue());
        return itemInfo;
    }

    private ItemStock convertStockFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemStock itemStock = new ItemStock();
        itemStock.setStock(itemModel.getStock());
        itemStock.setItemId(itemModel.getId());
        return itemStock;
    }

    private ItemModel convertModelFromItem(ItemInfo itemInfo, ItemStock itemStock) {
        if (itemInfo == null || itemStock == null) {
            return null;
        }
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemInfo, itemModel);
        itemModel.setPrice(new BigDecimal(itemInfo.getPrice()));
        itemModel.setStock(itemStock.getStock());
        return itemModel;
    }

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        ValidationResult result = this.validator.validate(itemModel);

        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

        ItemInfo itemInfo = convertInfoFromModel(itemModel);

        this.itemInfoMapper.insertSelective(itemInfo);
        itemModel.setId(itemInfo.getId());

        ItemStock itemStock = convertStockFromModel(itemModel);
        this.itemStockMapper.insertSelective(itemStock);

        return getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemInfo> itemInfoList = this.itemInfoMapper.listItem();

        // stream api
        List<ItemModel> itemModelList = itemInfoList.stream().map(itemInfo -> {
            ItemStock itemStock = this.itemStockMapper.selectByItemId(itemInfo.getId());
            ItemModel itemModel = convertModelFromItem(itemInfo, itemStock);
            return itemModel;
        }).collect(Collectors.toList());

        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemInfo itemInfo = this.itemInfoMapper.selectByPrimaryKey(id);
        if (itemInfo == null) {
            return null;
        }
        ItemStock itemStock = this.itemStockMapper.selectByItemId(id);
        ItemModel itemModel = convertModelFromItem(itemInfo, itemStock);
        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        int affectedRow = this.itemStockMapper.decreaseStock(itemId, amount);
        if (affectedRow > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        this.itemInfoMapper.increaseSales(itemId, amount);
    }
}
