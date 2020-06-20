package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    int userCount();
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    User getUserByUid(Integer uid);
    void registerUser(User user);
    List<User> searchUser(String subUsername, Integer maxNumber);
    void updateUser(User user);
    void updatePassword(String username, String password);
}
