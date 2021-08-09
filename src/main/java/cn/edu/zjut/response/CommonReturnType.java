package cn.edu.zjut.response;

/**
 * @author zett0n
 * @date 2021/8/8 15:58
 */
public class CommonReturnType {
    private Object data;
    private String status;

    public CommonReturnType() {}

    public CommonReturnType(Object data) {
        this.data = data;
        this.status = "success";
    }

    public CommonReturnType(Object data, String status) {
        this.status = status;
        this.data = data;
    }

    public static CommonReturnType create(Object data) {
        return new CommonReturnType(data);
    }

    public static CommonReturnType create(Object data, String status) {
        return new CommonReturnType(data, status);
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
