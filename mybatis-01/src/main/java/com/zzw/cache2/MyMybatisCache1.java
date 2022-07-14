package com.zzw.cache2;

import org.apache.ibatis.cache.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 用 Map 存储数据
 */
@SuppressWarnings("all")
public class MyMybatisCache1 implements Cache {

    private Map<Object, Object> internalCache = new HashMap<>();

    @Override
    public String getId() {
        return getClass().getName();
    }

    @Override
    public void putObject(Object key, Object value) {
        internalCache.put(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return internalCache.get(key);
    }

    @Override
    public Object removeObject(Object key) {
        return internalCache.remove(key);
    }

    @Override
    public void clear() {
        internalCache.clear();
    }

    @Override
    public int getSize() {
        return internalCache.size();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return new ReentrantReadWriteLock();
    }

}
