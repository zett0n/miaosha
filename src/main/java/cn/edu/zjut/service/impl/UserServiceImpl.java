package cn.edu.zjut.service.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.edu.zjut.dao.UserInfoMapper;
import cn.edu.zjut.dao.UserPasswordMapper;
import cn.edu.zjut.entity.UserInfo;
import cn.edu.zjut.entity.UserPasswordInfo;
import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.error.EmBusinessError;
import cn.edu.zjut.service.UserService;
import cn.edu.zjut.service.model.UserModel;
import cn.edu.zjut.validater.ValidationResult;
import cn.edu.zjut.validater.ValidatorImpl;

/**
 * @author zett0n
 * @date 2021/8/8 15:13
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserPasswordMapper userPasswordMapper;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private RedisTemplate redisTemplate;

    private UserModel convertModelFromUser(UserInfo userInfo, UserPasswordInfo userPasswordInfo) {
        if (userInfo == null || userPasswordInfo == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userInfo, userModel);
        userModel.setEncryptedPassword(userPasswordInfo.getEncryptPassword());

        return userModel;
    }

    private UserInfo convertInfoFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userModel, userInfo);
        return userInfo;
    }

    private UserPasswordInfo convertPasswordFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPasswordInfo userPasswordInfo = new UserPasswordInfo();
        userPasswordInfo.setEncryptPassword(userModel.getEncryptedPassword());
        userPasswordInfo.setUserId(userModel.getId());
        return userPasswordInfo;
    }

    @Override
    public UserModel getUserById(Integer id) {
        UserInfo userInfo = this.userInfoMapper.selectByPrimaryKey(id);
        if (userInfo == null) {
            return null;
        }
        UserPasswordInfo userPasswordInfo = this.userPasswordMapper.selectByUserId(id);
        return convertModelFromUser(userInfo, userPasswordInfo);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        // if (userModel == null || StringUtils.isEmpty(userModel.getName()) || userModel.getGender() == null
        // || userModel.getAge() == null || StringUtils.isEmpty(userModel.getTelephone())) {
        // throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        // }

        ValidationResult result = this.validator.validate(userModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

        UserInfo userInfo = convertInfoFromModel(userModel);
        try {
            this.userInfoMapper.insertSelective(userInfo);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号已被注册");
        }

        userModel.setId(userInfo.getId());

        UserPasswordInfo userPasswordInfo = convertPasswordFromModel(userModel);
        this.userPasswordMapper.insertSelective(userPasswordInfo);
    }

    @Override
    public UserModel validateLogin(String telephone, String encryptedPassword) throws BusinessException {
        UserInfo userInfo = this.userInfoMapper.selectByTelephone(telephone);

        if (userInfo == null) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordInfo userPasswordInfo = this.userPasswordMapper.selectByUserId(userInfo.getId());
        UserModel userModel = convertModelFromUser(userInfo, userPasswordInfo);

        if (!StringUtils.equals(encryptedPassword, userModel.getEncryptedPassword())) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

    @Override
    public UserModel getUserByIdInCache(Integer id) {
        UserModel userModel = (UserModel)this.redisTemplate.opsForValue().get("user_validate_" + id);
        if (userModel == null) {
            userModel = this.getUserById(id);
            this.redisTemplate.opsForValue().set("user_validate_" + id, userModel);
            this.redisTemplate.expire("user_validate_" + id, 10, TimeUnit.MINUTES);
        }
        return userModel;
    }
}
