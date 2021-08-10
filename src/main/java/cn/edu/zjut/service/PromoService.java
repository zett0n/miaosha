package cn.edu.zjut.service;

import cn.edu.zjut.service.model.PromoModel;

/**
 * @author zett0n
 * @date 2021/8/10 15:25
 */
public interface PromoService {
    PromoModel getPromoByItemId(Integer itemId);

}
