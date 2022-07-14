package com.zzw;

import com.zzw.xml.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class TestMybatis2 {

    private SqlSession sqlSession;

    /**
     * 目标代码
     */
    @Test
    public void target() throws Exception {
        // IO 方式打开输入流, 获取 mybatis-config.xml 和 XXXMapper.xml 文件的内容
        InputStream config = Resources.getResourceAsStream("mybatis-config.xml");

        // 根据 config 用 XPathParser 解析成 Configuration
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(config);

        sqlSession = factory.openSession();
    }

    /**
     * 测试 XPathParser
     */
    @Test
    public void testXml() throws Exception {
        Reader reader = Resources.getResourceAsReader("testxml/Users.xml");
        XPathParser xPathParser = new XPathParser(reader);

        List<XNode> xNodes = xPathParser.evalNodes("/users/*");
        System.out.println("xNodes size = " + xNodes.size());

        List<User> users = new ArrayList<>();
        Class clazz = User.class;
        for (XNode xNode : xNodes) {
            User user = new User();

            List<XNode> children = xNode.getChildren();
            for (XNode child : children) {
                String name = child.getName();
                String value = child.getStringBody();
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                field.set(user, value);
            }

            users.add(user);
        }

        System.out.println(users);
    }


}
