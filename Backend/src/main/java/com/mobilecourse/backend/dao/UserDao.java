package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    int userCount();
    User getUserByUsername(String username);
    void registerUser(User user);
}
