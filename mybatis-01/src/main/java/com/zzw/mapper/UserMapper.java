package com.zzw.mapper;

import com.zzw.pojo.User;

import java.util.List;

public interface UserMapper {

    //void save(User user);

    List<User> queryAllUsers();

}
