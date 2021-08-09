package cn.edu.zjut.dao;

import org.springframework.stereotype.Repository;

import cn.edu.zjut.entity.OrderInfo;

@Repository
public interface OrderInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    OrderInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);
}