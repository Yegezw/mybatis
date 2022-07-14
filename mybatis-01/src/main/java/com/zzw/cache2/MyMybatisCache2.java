package com.zzw.cache2;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.ibatis.cache.Cache;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * 用 Redis 存储数据
 */
@SuppressWarnings("all")
public class MyMybatisCache2 implements Cache {

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void putObject(Object key, Object value) {
        Jedis jedis = JedisUtil.getJedis();
        byte[] keyBytes = SerializationUtils.serialize((Serializable) key); // 序列化
        byte[] valueBytes = SerializationUtils.serialize((Serializable) value); // 序列化

        jedis.set(keyBytes, valueBytes);

        jedis.close();
    }

    @Override
    public Object getObject(Object key) {
        Jedis jedis = JedisUtil.getJedis();
        byte[] keyBytes = SerializationUtils.serialize((Serializable) key); // 序列化

        byte[] resBytes = jedis.get(keyBytes);
        Object res = SerializationUtils.deserialize(resBytes); // 反序列化

        jedis.close();
        return res;
    }

    @Override
    public Object removeObject(Object key) {
        Jedis jedis = JedisUtil.getJedis();
        byte[] keyBytes = SerializationUtils.serialize((Serializable) key); // 序列化

        Long res = jedis.hdel(keyBytes);

        jedis.close();
        return res;
    }

    @Override
    public void clear() {
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }

}
