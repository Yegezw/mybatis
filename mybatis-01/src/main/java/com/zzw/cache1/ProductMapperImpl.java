package com.zzw.cache1;

/**
 * 演示代理缓存
 */
public class ProductMapperImpl implements ProductMapper {

    @Override
    public void save() {
        System.out.println("JDBC 的方式操作数据库, 完成插入操作");
    }

    @Override
    public Product queryProductById(int id) {
        System.out.println("JDBC 的方式基于 ID 进行查询 " + id);
        return new Product();
    }

}
