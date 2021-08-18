package cn.edu.zjut.service.impl;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import cn.edu.zjut.dao.PromoInfoMapper;
import cn.edu.zjut.entity.PromoInfo;
import cn.edu.zjut.service.ItemService;
import cn.edu.zjut.service.PromoService;
import cn.edu.zjut.service.UserService;
import cn.edu.zjut.service.model.ItemModel;
import cn.edu.zjut.service.model.PromoModel;
import cn.edu.zjut.service.model.UserModel;

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

    @Autowired
    private UserService userService;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        // 获取对应商品的秒杀活动信息
        PromoInfo promoInfo = this.promoInfoMapper.selectByItemId(itemId);
        PromoModel promoModel = convertModelFromInfo(promoInfo);
        if (promoModel == null) {
            return null;
        }

        // 判断当前时间是否秒杀活动即将开始或正在进行
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
            // 将库存同步到redis内
            this.redisTemplate.opsForValue().set("promo_item_stock_" + itemModel.getId(), itemModel.getStock());

            // 设置秒杀令牌的上限
            this.redisTemplate.opsForValue().set("promo_token_num_" + promoId, 5 * itemModel.getStock());

        }
    }

    @Override
    public String generatePromoToken(Integer userId, Integer itemId, Integer promoId) {
        // 用户是否合法
        UserModel userModel = this.userService.getUserByIdInCache(userId);
        if (userModel == null) {
            return null;
        }

        // 下单的商品是否存在
        ItemModel itemModel = this.itemService.getItemByIdInCache(itemId);
        if (itemModel == null) {
            return null;
        }

        // 获取对应商品的秒杀活动信息
        PromoInfo promoInfo = this.promoInfoMapper.selectByPrimaryKey(promoId);
        PromoModel promoModel = convertModelFromInfo(promoInfo);
        if (promoModel == null) {
            return null;
        }

        // 判断当前时间是否秒杀活动即将开始或正在进行
        if (promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(1);
        } else if (promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(3);
        } else {
            promoModel.setStatus(2);
        }
        if (promoModel.getStatus() != 2) {
            return null;
        }

        // 获取秒杀大闸的数量
        Long leftPromoTokenNum = this.redisTemplate.opsForValue().increment("promo_token_num_" + promoId, -1);
        if (leftPromoTokenNum == null || leftPromoTokenNum <= 0) {
            return null;
        }

        // 生成promoToken并且存入redis，设置5分钟有效期
        String promoToken = UUID.randomUUID().toString().replace("-", "");
        this.redisTemplate.opsForValue().set("user_id_" + userId + "_item_id_" + itemId + "_promo_token_" + promoId,
            promoToken);
        this.redisTemplate.expire("user_id_" + userId + "_item_id_" + itemId + "_promo_token_" + promoId, 5,
            TimeUnit.MINUTES);

        return promoToken;
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
