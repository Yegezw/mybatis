package com.zzw;

import com.zzw.cache1.ProductMapper;
import com.zzw.cache1.ProductMapperImpl;
import com.zzw.cache1.ProxyCache;
import org.apache.ibatis.cache.decorators.LoggingCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

@SuppressWarnings("all")
public class TestMybatis3 {

    private SqlSession sqlSession;

    @Before
    public void init() throws Exception {
        InputStream config = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(config);
        sqlSession = factory.openSession();
    }

    @After
    public void release() {
        sqlSession.close();
    }

    /**
     * 测试代理缓存
     */
    @Test
    public void testProxyCache() {
        ProductMapper productMapper = ProxyCache.getProxy(new ProductMapperImpl());

        System.out.println("-----------");
        productMapper.save(); // 非查询方法不做缓存处理(可能还需要清空缓存)
        System.out.println("-----------");
        productMapper.queryProductById(1); // 缓存命中
        System.out.println("-----------");
        productMapper.queryProductById(2); // 缓存未命中
        System.out.println("-----------");
    }

    /**
     * 测试 Cache 和装饰器
     */
    @Test
    public void test() {
        PerpetualCache perpetualCache = new PerpetualCache("zzw"); // 核心 Cache

        LruCache lruCache = new LruCache(perpetualCache); // 增强换出

        LoggingCache loggingCache = new LoggingCache(lruCache); // 增强日志
    }

}
