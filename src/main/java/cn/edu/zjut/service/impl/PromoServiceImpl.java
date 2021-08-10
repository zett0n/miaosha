package cn.edu.zjut.service.impl;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.edu.zjut.dao.PromoInfoMapper;
import cn.edu.zjut.entity.PromoInfo;
import cn.edu.zjut.service.PromoService;
import cn.edu.zjut.service.model.PromoModel;

/**
 * @author zett0n
 * @date 2021/8/10 15:26
 */
@Service
public class PromoServiceImpl implements PromoService {
    @Autowired
    private PromoInfoMapper promoInfoMapper;

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
