package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    int userCount();
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    User getUserById(Integer id);
    void registerUser(User user);
    List<User> searchUser(String subUsername, Integer maxNumber);
    void updateUser(User user);
    void updatePassword(Integer id, String password);
}
