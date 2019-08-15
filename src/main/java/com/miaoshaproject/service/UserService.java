package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BusinessException;

    /*
    * password使用户密码加密后的密码
    * */
    UserModel validateLogin(String telphone,String encrptPassword) throws BusinessException;
}
