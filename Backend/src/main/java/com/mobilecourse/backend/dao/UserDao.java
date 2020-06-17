package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    int userCount();
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    void registerUser(User user);
    List<User> searchUser(String subUsername, Integer maxNumber);
}
