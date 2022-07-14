package com.zzw;

import com.mysql.jdbc.Driver;
import com.zzw.mapper.UserMapper;
import com.zzw.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@SuppressWarnings("all")
public class TestMybatis1 {

    private SqlSession sqlSession;

    private final String driver = "com.mysql.jdbc.Driver";
    private final String url = "jdbc:mysql://localhost:3306/sg_mybatis?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    private final String username = "root";
    private final String password = "root";

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
     * 回顾 JDBC
     */
    @Test
    public void testJDBC() throws Exception {
        Class.forName(driver);
        Driver driver = new Driver();
        DriverManager.registerDriver(driver);
        Connection connection = DriverManager.getConnection(url, username, password);

        String sql = "select * from user";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
    }

    /**
     * 回顾 1
     */
    @Test
    public void test1() {
        List<User> users = sqlSession.selectList("com.zzw.mapper.UserMapper.queryAllUsers");
        users.forEach(System.out::println);
    }

    /**
     * 回顾 2
     */
    @Test
    public void test2() {
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        List<User> users = userMapper.queryAllUsers();
        users.forEach(System.out::println);
    }

    /**
     * 测试代理
     */
    @Test
    public void testProxy() {
        ClassLoader classLoader = TestMybatis1.class.getClassLoader();
        Class[] interfaces = {UserMapper.class};
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("queryAllUsers")) {
                    // namespace.id (接口名.方法名)
                    List<User> users = sqlSession.selectList("com.zzw.mapper.UserMapper.queryAllUsers");
                    return users;
                }
                return null;
            }
        };

        UserMapper userMapper = (UserMapper) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler); // 动态代理
        List<User> users = userMapper.queryAllUsers();
        users.forEach(System.out::println);
    }

}
