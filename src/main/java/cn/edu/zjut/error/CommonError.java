package cn.edu.zjut.error;

/**
 * @author zett0n
 * @date 2021/8/8 16:07
 */
public interface CommonError {
    int getErrCode();

    String getErrMsg();

    CommonError setErrMsg(String errMsg);
}
