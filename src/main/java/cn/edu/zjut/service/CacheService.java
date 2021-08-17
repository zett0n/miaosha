package cn.edu.zjut.service;

/**
 * @author zett0n
 * @date 2021/8/17 13:24
 */
// 封装本地缓存操作类
public interface CacheService {
    // 存方法
    void setCommonCache(String key, Object value);

    // 取方法
    Object getFromCommonCache(String key);
}
