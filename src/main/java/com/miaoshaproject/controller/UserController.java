package com.miaoshaproject.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.ViewObject.UserVo;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


@Controller("user")
@RequestMapping("/user")
//springboot对应返回web请求中加上跨域Access-Control-Allow-Head 对应的标签为*即所有都可以
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping(value="/login"/*,method={RequestMethod.GET},consumes = {CONTENT_TYPE_FORMED}*/)
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone") String telphone,@RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if(org.apache.commons.lang3.StringUtils.isEmpty(telphone)||StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        UserModel userModel=userService.validateLogin(telphone,password);

        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);
        return CommonReturnType.create(null);
    }

    @RequestMapping(value="/register"/*,method={RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED}*/)
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name ="otpCode") String otpCode,
                                     @RequestParam(name="name") String name,
                                     @RequestParam(name = "gender") Integer gender,
                                     @RequestParam(name = "password") String password,
                                     @RequestParam(name = "age")Integer age) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //校验
        String inSessionOtp=(String)this.httpServletRequest.getSession().getAttribute(telphone);
        if(!StringUtils.equals(inSessionOtp,otpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证失败");
        }
        //业务:用户的注册流程
        UserModel userModel=new UserModel();
        userModel.setName(name);
        userModel.setTelphone(telphone);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userModel.setThirdPartyId("byphone");
        userModel.setAge(age);

        userService.register(userModel);
        return CommonReturnType.create(null);

    }



    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        BASE64Encoder base64en=new BASE64Encoder();
        //加密字符串
        String newStr=base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }


    @RequestMapping(value="/getOtp",method={RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone){
        Random random=new Random();
        int randomInt=random.nextInt(9999);
        randomInt+=10000;
        String otpCode=String.valueOf(randomInt);
        httpServletRequest.getSession().setAttribute(telphone,otpCode);
        return CommonReturnType.create(null);

    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id")Integer id) throws BusinessException {
        UserModel userModel=userService.getUserById(id);
        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        UserVo userVo=convertFromModel(userModel);
        return CommonReturnType.create(userVo);
    }

    private UserVo convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }
        UserVo userVo=new UserVo();
        BeanUtils.copyProperties(userModel,userVo);
        return userVo;
    }

}
