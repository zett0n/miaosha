package cn.edu.zjut.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.edu.zjut.entity.ItemStockInfo;

@Repository
public interface ItemStockMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemStockInfo record);

    int insertSelective(ItemStockInfo record);

    ItemStockInfo selectByPrimaryKey(Integer id);

    ItemStockInfo selectByItemId(Integer itemId);

    int updateByPrimaryKeySelective(ItemStockInfo record);

    int updateByPrimaryKey(ItemStockInfo record);

    int decreaseStock(@Param("itemId") Integer itemId, @Param("amount") Integer amount);
}