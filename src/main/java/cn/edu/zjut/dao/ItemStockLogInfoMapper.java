package cn.edu.zjut.dao;

import org.springframework.stereotype.Repository;

import cn.edu.zjut.entity.ItemStockLogInfo;

@Repository
public interface ItemStockLogInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(ItemStockLogInfo record);

    int insertSelective(ItemStockLogInfo record);

    ItemStockLogInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ItemStockLogInfo record);

    int updateByPrimaryKey(ItemStockLogInfo record);
}