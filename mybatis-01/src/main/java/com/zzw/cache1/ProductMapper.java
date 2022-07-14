package com.zzw.cache1;

/**
 * 演示代理缓存
 */
public interface ProductMapper {

    void save();

    @Cache
    Product queryProductById(int id);

}
