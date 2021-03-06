package com.zzw;

import com.zzw.cache1.ProductMapper;
import com.zzw.cache1.ProductMapperImpl;
import com.zzw.cache1.ProxyCache;
import com.zzw.mapper.UserMapper;
import com.zzw.pojo.User;
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
import java.util.List;

@SuppressWarnings("all")
public class TestMybatis3 {

    private SqlSessionFactory factory;
    private SqlSession sqlSession;

    @Before
    public void init() throws Exception {
        InputStream config = Resources.getResourceAsStream("mybatis-config.xml");
        factory = new SqlSessionFactoryBuilder().build(config);
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

    /**
     * 测试一级缓存
     */
    @Test
    public void testLevel1Cache1() {
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        List<User> users1 = userMapper.queryAllUsers(); // 第一次查询
        users1.forEach(System.out::println);

        System.out.println("-------------------------");

        List<User> users2 = userMapper.queryAllUsers(); // 第二次查询
        users2.forEach(System.out::println);
    }

    /**
     * 测试一级缓存只对同一个 SqlSession 有效
     */
    @Test
    public void testLevel1Cache2() {
        SqlSession sqlSession1 = factory.openSession();
        SqlSession sqlSession2 = factory.openSession();

        UserMapper userMapper1 = sqlSession1.getMapper(UserMapper.class);
        UserMapper userMapper2 = sqlSession2.getMapper(UserMapper.class);

        List<User> users1 = userMapper1.queryAllUsers(); // 第一次 sqlSession1 查询
        users1.forEach(System.out::println);

        System.out.println("-------------------------");

        List<User> users2 = userMapper2.queryAllUsers(); // 第二次 sqlSession2 查询
        users2.forEach(System.out::println);

        sqlSession1.close();
        sqlSession2.close();
    }

    /**
     * 测试二级缓存
     */
    @Test
    public void testLevel2Cache() {
        SqlSession sqlSession1 = factory.openSession();
        SqlSession sqlSession2 = factory.openSession();

        UserMapper userMapper1 = sqlSession1.getMapper(UserMapper.class);
        UserMapper userMapper2 = sqlSession2.getMapper(UserMapper.class);

        List<User> users1 = userMapper1.queryAllUsers(); // 第一次 sqlSession1 查询
        users1.forEach(System.out::println);
        sqlSession1.commit();                            // 提交 sqlSession1

        System.out.println("-------------------------");

        List<User> users2 = userMapper2.queryAllUsers(); // 第二次 sqlSession2 查询
        users2.forEach(System.out::println);
        sqlSession2.commit();                            // 提交 sqlSession2

        sqlSession1.close();
        sqlSession2.close();
    }

    /**
     * 测试二级缓存清空
     */
    @Test
    public void testLevel2Cache2() {
        SqlSession sqlSession1 = factory.openSession();
        SqlSession sqlSession2 = factory.openSession();
        SqlSession sqlSession3 = factory.openSession();

        UserMapper userMapper1 = sqlSession1.getMapper(UserMapper.class);
        UserMapper userMapper2 = sqlSession2.getMapper(UserMapper.class);
        UserMapper userMapper3 = sqlSession3.getMapper(UserMapper.class);

        List<User> users1 = userMapper1.queryAllUsers(); // 第一次 sqlSession1 查询 ---> ms1 ---> cache
        User user1 = userMapper1.queryUserById(4);       // 第一次 sqlSession1 查询 ---> ms2 ---> cache
        sqlSession1.commit();                            // 提交 sqlSession1, 会往二级缓存中添加数据

        System.out.println("-------------------------");

        List<User> users2 = userMapper2.queryAllUsers(); // 第二次 sqlSession2 查询 ---> ms1 ---> 查询 cache
        User user2 = userMapper1.queryUserById(4);       // 第一次 sqlSession2 查询 ---> ms2 ---> 查询 cache
        sqlSession2.commit();                            // 提交 sqlSession2

        System.out.println("-------------------------");

        User user = new User(1, "zzw1");
        Integer rows = userMapper3.updateUserById(user); // sqlSession3 更新 ---> ms3 ---> 清空 cache
        System.out.println(rows);
        sqlSession3.commit();                            // 提交 sqlSession3

        sqlSession1.close();
        sqlSession2.close();
        sqlSession3.close();
    }

}
