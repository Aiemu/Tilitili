package com.mobilecourse.backend.controllers;

import com.mobilecourse.backend.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@EnableAutoConfiguration
@RequestMapping("/user")
public class UserController extends CommonController {
    @Autowired
    private UserDao userMapper;

    /**
     * 查看注册用户的个数.
     * @return
     */
    @RequestMapping(value = "/user/stat")
    public String getUserCount() {
        return wrapperMsg(200, "Tilitili当前一共有" + userMapper.userCount() + "名用户.");
    }


}
