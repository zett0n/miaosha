package cn.edu.zjut.dao;

import org.springframework.stereotype.Repository;

import cn.edu.zjut.entity.SequenceInfo;

@Repository
public interface SequenceInfoMapper {
    int deleteByPrimaryKey(String name);

    int insert(SequenceInfo record);

    int insertSelective(SequenceInfo record);

    SequenceInfo selectByPrimaryKey(String name);

    SequenceInfo getSequenceByName(String name);

    int updateByPrimaryKeySelective(SequenceInfo record);

    int updateByPrimaryKey(SequenceInfo record);
}