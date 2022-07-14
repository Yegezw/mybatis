package com.zzw.cache2;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis 获取连接工具类
 */
public class JedisUtil {

    private static final JedisPool jp; // 连接池

    private static final String host = "127.0.0.1";
    private static final int port = 6379;

    private static final int maxTotal = 30;
    private static final int maxIdle = 10;

    static {
        JedisPoolConfig config = new JedisPoolConfig(); // 连接池配置
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);

        jp = new JedisPool(config, host, port);
    }

    public static Jedis getJedis() {
        return jp.getResource();
    }

}