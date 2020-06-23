package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    //统计已注册用户个数
    int userCount();
    //根据用户名获取具体用户
    User getUserByUsername(String username);
    //根据邮箱获取具体用户
    User getUserByEmail(String email);
    //根据Uid获取具体用户
    User getUserByUid(Integer uid);
    //用户注册
    void registerUser(User user);
    //根据字段搜索昵称包含此字段的用户
    List<User> searchUser(String subUsername, Integer maxNumber);
    //更新用户信息
    void updateUser(User user);
    //修改密码
    void updatePassword(String username, String password);
}
