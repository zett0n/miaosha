package cn.edu.zjut.validater;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zett0n
 * @date 2021/8/9 16:16
 */
public class ValidationResult {
    // 校验结果是否有错
    private boolean hasErrors = false;
    // 存放错误信息的Map
    private Map<String, String> errorMsgMap = new HashMap<>();

    // 实现通过格式化字符串信息获取错误结果的msg方法
    public String getErrMsg() {
        return StringUtils.join(this.errorMsgMap.values().toArray(), ",");
    }

    public boolean isHasErrors() {
        return this.hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrorMsgMap() {
        return this.errorMsgMap;
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }
}
