package cn.edu.zjut.error;

/**
 * @author zett0n
 * @date 2021/8/8 16:08
 */
public enum EmBusinessError implements CommonError {
    // 10000开头为通用错误定义
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"), UNKNOWN_ERROR(10002, "未知错误"),
    // 20000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001, "用户不存在"), USER_LOGIN_FAIL(20002, "用户手机号和密码不正确"), USER_NOT_LOGIN(20003, "用户还未登录"),
    // 30000开头为商品信息相关错误定义
    STOCK_NOT_ENOUGH(30001, "库存不足"), MQ_SEND_FAIL(30002, "库存异步消息失败"), RATE_LIMIT(30003, "服务器繁忙，请稍后再试");

    private int errCode;
    private String errMsg;

    EmBusinessError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
