package cn.edu.zjut.service;

import cn.edu.zjut.service.model.PromoModel;

/**
 * @author zett0n
 * @date 2021/8/10 15:25
 */
public interface PromoService {
    // 根据item_id获取即将进行或正在进行的秒杀活动
    PromoModel getPromoByItemId(Integer itemId);

    // 发布秒杀活动
    void publishPromo(Integer promoId);

    // 生成秒杀用的令牌
    String generatePromoToken(Integer userId, Integer itemId, Integer promoId);
}
