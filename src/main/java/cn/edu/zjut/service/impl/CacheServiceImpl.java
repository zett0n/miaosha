package cn.edu.zjut.service.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cn.edu.zjut.service.CacheService;

/**
 * @author zett0n
 * @date 2021/8/17 13:25
 */
@Service
public class CacheServiceImpl implements CacheService {
    private Cache<String, Object> commonCache;

    @PostConstruct
    public void init() {
        this.commonCache = CacheBuilder.newBuilder()
            // 缓存容器初始容量10
            .initialCapacity(10)
            // 缓存容器最大储存100个key，超出后按LRU策略移除
            .maximumSize(100)
            // 设置写缓存后多少秒过期
            .expireAfterWrite(60, TimeUnit.SECONDS).build();
    }

    @Override
    public void setCommonCache(String key, Object value) {
        this.commonCache.put(key, value);
    }

    @Override
    public Object getFromCommonCache(String key) {
        return this.commonCache.getIfPresent(key);
    }
}
