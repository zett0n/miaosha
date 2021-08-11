package cn.edu.zjut.controller;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

/**
 * @author zett0n
 * @date 2021/8/8 18:08
 */
public class BaseController {
    // 定义handlerException解决未被controller层吸收的异常
    // @ExceptionHandler(Exception.class)
    // @ResponseStatus(HttpStatus.OK)
    // @ResponseBody
    // public Object handlerException(HttpServletRequest request, Exception e) {
    // Map<String, Object> responseData = new HashMap<>();
    //
    // if (e instanceof BusinessException) {
    // BusinessException businessException = (BusinessException)e;
    // responseData.put("errCode", businessException.getErrCode());
    // responseData.put("errMsg", businessException.getErrMsg());
    // } else {
    // responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
    // responseData.put("errMsg", EmBusinessError.UNKNOWN_ERROR.getErrMsg());
    // }
    //
    // return CommonReturnType.create(responseData, "fail");
    // }

    public String EncodeByMd5(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
    }
}
