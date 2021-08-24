package cn.edu.zjut.controller;

/**
 * @author zett0n
 * @date 2021/8/11 17:43
 */

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import cn.edu.zjut.error.BusinessException;
import cn.edu.zjut.error.EmBusinessError;
import cn.edu.zjut.response.CommonReturnType;

// 面向Controller切面编程
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class) // 抛出异常被ExceptionHandler捕获后就进入下面的doError方法
    public CommonReturnType doError(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        Map<String, Object> responseData = new HashMap<>();
        if (ex instanceof BusinessException) {
            ex.printStackTrace();
            BusinessException BusinessException = (BusinessException)ex;
            responseData.put("errCode", BusinessException.getErrCode());
            responseData.put("errMsg", BusinessException.getErrMsg());
        } else if (ex instanceof ServletRequestBindingException) {
            // @RequestParam是必传的，如果没传，就会触发这个异常
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg", "URL绑定路由问题");
        } else if (ex instanceof NoHandlerFoundException) {
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg", "没有找到对应的访问路径");
        } else {
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg", EmBusinessError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseData, "fail");
    }
}