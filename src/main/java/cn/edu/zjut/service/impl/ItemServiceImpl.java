package cn.edu.zjut.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.edu.zjut.dao.ItemInfoMapper;
import cn.edu.zjut.dao.ItemStockMapper;
import cn.edu.zjut.entity.ItemInfo;
import cn.edu.zjut.entity.ItemStockInfo;
import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.error.EmBusinessError;
import cn.edu.zjut.mq.MQProducer;
import cn.edu.zjut.service.ItemService;
import cn.edu.zjut.service.PromoService;
import cn.edu.zjut.service.model.ItemModel;
import cn.edu.zjut.service.model.PromoModel;
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

    @Autowired
    private PromoService promoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQProducer mqProducer;

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

    private ItemStockInfo convertStockFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemStockInfo itemStockInfo = new ItemStockInfo();
        itemStockInfo.setStock(itemModel.getStock());
        itemStockInfo.setItemId(itemModel.getId());
        return itemStockInfo;
    }

    private ItemModel convertModelFromItem(ItemInfo itemInfo, ItemStockInfo itemStockInfo) {
        if (itemInfo == null || itemStockInfo == null) {
            return null;
        }
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemInfo, itemModel);
        itemModel.setPrice(new BigDecimal(itemInfo.getPrice()));
        itemModel.setStock(itemStockInfo.getStock());
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

        ItemStockInfo itemStockInfo = convertStockFromModel(itemModel);
        this.itemStockMapper.insertSelective(itemStockInfo);

        return getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemInfo> itemInfoList = this.itemInfoMapper.listItem();

        // stream api
        List<ItemModel> itemModelList = itemInfoList.stream().map(itemInfo -> {
            ItemStockInfo itemStockInfo = this.itemStockMapper.selectByItemId(itemInfo.getId());
            return convertModelFromItem(itemInfo, itemStockInfo);
        }).collect(Collectors.toList());

        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemInfo itemInfo = this.itemInfoMapper.selectByPrimaryKey(id);
        if (itemInfo == null) {
            return null;
        }
        ItemStockInfo itemStockInfo = this.itemStockMapper.selectByItemId(id);
        ItemModel itemModel = convertModelFromItem(itemInfo, itemStockInfo);

        // 获取秒杀相关信息
        PromoModel promoModel = this.promoService.getPromoByItemId(itemModel.getId());
        // TODO 硬编码
        if (promoModel != null && promoModel.getStatus() != 3) {
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    @Override
    public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
        return this.mqProducer.asyncReduceStock(itemId, amount);
    }

    @Override
    public boolean increaseStock(Integer itemId, Integer amount) {
        this.redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount);
        return true;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        // int leftItemStock = this.itemStockMapper.decreaseStock(itemId, amount);
        Long leftItemStock = this.redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount * (-1L));

        // reids更新库存失败
        if (leftItemStock < 0) {
            this.increaseStock(itemId, amount);
            return false;
        }
        // reids更新库存成功
        return true;
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        this.itemInfoMapper.increaseSales(itemId, amount);
    }

    @Override
    public ItemModel getItemByIdInCache(Integer id) {
        ItemModel itemModel = (ItemModel)this.redisTemplate.opsForValue().get("item_validate_" + id);
        if (itemModel == null) {
            itemModel = this.getItemById(id);
            this.redisTemplate.opsForValue().set("item_validate_" + id, itemModel);
            this.redisTemplate.expire("item_validate_" + id, 10, TimeUnit.MINUTES);
        }
        return itemModel;
    }
}
