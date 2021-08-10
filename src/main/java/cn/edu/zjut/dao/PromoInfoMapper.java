package cn.edu.zjut.dao;

import org.springframework.stereotype.Repository;

import cn.edu.zjut.entity.PromoInfo;

@Repository
public interface PromoInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PromoInfo record);

    int insertSelective(PromoInfo record);

    PromoInfo selectByPrimaryKey(Integer id);

    PromoInfo selectByItemId(Integer itemId);

    int updateByPrimaryKeySelective(PromoInfo record);

    int updateByPrimaryKey(PromoInfo record);
}