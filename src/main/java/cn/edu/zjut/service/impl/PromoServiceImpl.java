package cn.edu.zjut.service.impl;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.edu.zjut.dao.PromoInfoMapper;
import cn.edu.zjut.entity.PromoInfo;
import cn.edu.zjut.service.ItemService;
import cn.edu.zjut.service.PromoService;
import cn.edu.zjut.service.model.ItemModel;
import cn.edu.zjut.service.model.PromoModel;

/**
 * @author zett0n
 * @date 2021/8/10 15:26
 */
@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoInfoMapper promoInfoMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        // 获取对应商品的秒杀活动信息
        PromoInfo promoInfo = this.promoInfoMapper.selectByItemId(itemId);
        PromoModel promoModel = convertModelFromInfo(promoInfo);
        if (promoModel == null) {
            return null;
        }

        // 判断当前时间是否秒杀活动即将开始或正在进行
        DateTime now = new DateTime();
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }
        return promoModel;
    }

    @Override
    public void publishPromo(Integer promoId) {
        // 通过活动id获取活动
        PromoInfo promoInfo = this.promoInfoMapper.selectByPrimaryKey(promoId);
        Integer itemId = promoInfo.getItemId();
        if (itemId != null && itemId != 0) {
            ItemModel itemModel = this.itemService.getItemById(itemId);
            this.redisTemplate.opsForValue().set("promo_item_stock_" + itemModel.getId(), itemModel.getStock());
        }
    }

    private PromoModel convertModelFromInfo(PromoInfo promoInfo) {
        if (promoInfo == null) {
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoInfo, promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoInfo.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoInfo.getStartDate()));
        promoModel.setEndDate(new DateTime(promoInfo.getEndDate()));

        return promoModel;
    }
}
