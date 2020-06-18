package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.annotation.LoginAuth;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.exception.BusinessException;
import com.mobilecourse.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;
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
    @RequestMapping(value = "/stat")
    public String getUserCount() {
        return wrapperMsg(200, "Tilitili当前一共有" + userMapper.userCount() + "名用户.");
    }

    @RequestMapping(value = "/profile/info/{id}", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getInfo(@PathVariable(value = "id") Integer id) {
        User user = userMapper.getUserById(id);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, 1, "The user does not exist.");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", user.getEmail());
        jsonObject.put("nickname", user.getNickname());
        jsonObject.put("department", user.getDepartment());
        jsonObject.put("organization", user.getOrganization());
        jsonObject.put("joinAt", user.getJoinAt());
        jsonObject.put("bio", user.getBio());
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/profile/edit", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> editInfo(@RequestParam(value = "nickname") String nickname,
                                               HttpSession session) {
        Integer id = (Integer) session.getAttribute("id");
        return wrapperResponse(HttpStatus.OK, new JSONObject());
    }


}
