package com.miaoshaproject.error;


//包装器业务异常实现
public class BusinessException extends Exception implements CommonError {
    private CommonError commonError;

    //直接接受EmBusinessError的传参用于构造业务异常
    public BusinessException (CommonError commonError){
        super();
        this.commonError=commonError;
    }

    //接受自定义errMsg的方式构造业务异常
    public BusinessException(CommonError commonError,String errMsg){
        super();
        this.commonError=commonError;
        this.commonError.setErrorMessage(errMsg);
    }



    @Override
    public int getErrorCode() {
        return this.commonError.getErrorCode();
    }

    @Override
    public String getErrorMessage() {
        return this.commonError.getErrorMessage();
    }

    @Override
    public CommonError setErrorMessage(String errorMessage) {
        this.commonError.setErrorMessage(errorMessage);
        return this;
    }
}
