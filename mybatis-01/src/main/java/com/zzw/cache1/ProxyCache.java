package com.zzw.cache1;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 演示代理缓存
 */
@SuppressWarnings("all")
public class ProxyCache {

    public static <T> T getProxy(T obj) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        Class[] interfaces = obj.getClass().getInterfaces();
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 查询方法才会进行缓存处理
                if (method.isAnnotationPresent(Cache.class)) {
                    System.out.println("连接 Redis, 先去缓存查询");

                    if (args[0].equals(1)) {
                        System.out.println("Redis 查到了");
                        return new Product(1, "Redis 查到了", 10.0); // 返回结果
                    } else {
                        System.out.println("Redis 没查到");
                        Object res = method.invoke(obj, args); // 去数据库查询
                        System.out.println("把从数据库查询的结果缓存到 Redis 中");
                        return res; // 返回结果
                    }
                }

                // 非查询方法不做缓存处理(可能还需要清空缓存)
                return method.invoke(obj, args);
            }
        };

        return (T) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }

}
