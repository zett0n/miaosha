package cn.edu.zjut.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.edu.zjut.dao.UserInfoMapper;
import cn.edu.zjut.dao.UserPasswordMapper;
import cn.edu.zjut.entity.UserInfo;
import cn.edu.zjut.entity.UserPassword;
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

    private UserModel convertFromUser(UserInfo userInfo, UserPassword userPassword) {
        if (userInfo == null || userPassword == null) {
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userInfo, userModel);
        userModel.setEncryptedPassword(userPassword.getEncryptPassword());

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

    private UserPassword convertPasswordFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPassword userPassword = new UserPassword();
        userPassword.setEncryptPassword(userModel.getEncryptedPassword());
        userPassword.setUserId(userModel.getId());
        return userPassword;
    }

    @Override
    public UserModel getUserById(Integer id) {
        UserInfo userInfo = this.userInfoMapper.selectByPrimaryKey(id);
        if (userInfo == null) {
            return null;
        }
        UserPassword userPassword = this.userPasswordMapper.selectByUserId(id);
        return convertFromUser(userInfo, userPassword);
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

        UserPassword userPassword = convertPasswordFromModel(userModel);
        this.userPasswordMapper.insertSelective(userPassword);
    }

    @Override
    public UserModel validateLogin(String telephone, String encryptedPassword) throws BusinessException {
        UserInfo userInfo = this.userInfoMapper.selectByTelephone(telephone);

        if (userInfo == null) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPassword userPassword = this.userPasswordMapper.selectByUserId(userInfo.getId());
        UserModel userModel = convertFromUser(userInfo, userPassword);

        if (!StringUtils.equals(encryptedPassword, userModel.getEncryptedPassword())) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }
}
