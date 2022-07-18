package com.zzw.mapper;

import com.zzw.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    //void save(User user);

    List<User> queryAllUsers();

    User queryUserById(@Param("id") Integer id);

    Integer updateUserById(User user);

}
