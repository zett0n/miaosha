package cn.edu.zjut.error;

/**
 * @author zett0n
 * @date 2021/8/8 16:08
 */
public enum EmBusinessError implements CommonError {
    // 通用错误类型00001
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"), UNKNOWN_ERROR(10002, "未知错误"),
    // 10000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001, "用户不存在"), USER_LOGIN_FAIL(20002, "用户手机号和密码不正确");

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
