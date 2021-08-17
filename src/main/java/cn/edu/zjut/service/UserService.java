package cn.edu.zjut.service;

import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.service.model.UserModel;

/**
 * @author zett0n
 * @date 2021/8/8 15:13
 */
public interface UserService {
    UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BusinessException;

    UserModel validateLogin(String telephone, String encryptedPassword) throws BusinessException;

    UserModel getUserByIdInCache(Integer id);
}
