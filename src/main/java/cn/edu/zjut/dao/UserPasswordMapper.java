package cn.edu.zjut.dao;

import org.springframework.stereotype.Repository;

import cn.edu.zjut.entity.UserPasswordInfo;

@Repository
public interface UserPasswordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserPasswordInfo record);

    int insertSelective(UserPasswordInfo record);

    UserPasswordInfo selectByPrimaryKey(Integer id);

    UserPasswordInfo selectByUserId(Integer userId);

    int updateByPrimaryKeySelective(UserPasswordInfo record);

    int updateByPrimaryKey(UserPasswordInfo record);
}