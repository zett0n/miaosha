package cn.edu.zjut.response;

/**
 * @author zett0n
 * @date 2021/8/8 15:58
 */
public class CommonReturnType {
    // success or fail
    private String status;

    // 如果success，data内返回前端需要的JSON数据
    // 如果fail，data内使用通用的错误码格式
    private Object data;

    private CommonReturnType(Object data) {
        this.data = data;
        this.status = "success";
    }

    private CommonReturnType(Object data, String status) {
        this.status = status;
        this.data = data;
    }

    public static CommonReturnType create(Object data) {
        return new CommonReturnType(data);
    }

    public static CommonReturnType create(Object data, String status) {
        return new CommonReturnType(data, status);
    }
}
