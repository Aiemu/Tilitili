package com.mobilecourse.backend.controllers;

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

@RestController
@EnableAutoConfiguration
@RequestMapping("/user")
public class UserController extends CommonController {
    @Autowired
    private UserDao userDao;

    /**
     * 查看注册用户的个数.
     * @return
     */
    @RequestMapping(value = "/stat")
    public String getUserCount() {
        return wrapperMsg(200, "Tilitili当前一共有" + userDao.userCount() + "名用户.");
    }

    @RequestMapping(value = "/profile/info/{id}", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getInfo(@PathVariable(value = "id") Integer id) {
        User user = userDao.getUserById(id);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, 1, "The user does not exist.");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", user.getEmail());
        jsonObject.put("nickname", user.getNickname());
        jsonObject.put("department", user.getDepartment());
        jsonObject.put("joinAt", user.getJoinAt());
        jsonObject.put("bio", user.getBio());
        jsonObject.put("avatar", user.getAvatar());
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/profile/edit", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> editInfo(@RequestParam(value = "nickname", required = false) String nickname,
                                               @RequestParam(value = "bio", required = false) String bio,
                                               @RequestParam(value = "avatar", required = false) String avatar,
                                               HttpSession session) {
        Integer id = (Integer) session.getAttribute("id");
        User updateUser = new User();
        updateUser.setNickname(nickname);
        updateUser.setBio(bio);
        updateUser.setAvatar(avatar);
        updateUser.setId(id);
        userDao.updateUser(updateUser);
        if (nickname != null) {
            session.setAttribute("nickname", nickname);
        }
        return wrapperResponse(HttpStatus.OK, new JSONObject());
    }

    @RequestMapping(value = "/password", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> updatePassword(@RequestParam(value = "id") Integer id,
                                                     @RequestParam(value = "old") String oldPassword,
                                                     @RequestParam(value = "new") String newPassword) {
        User user = userDao.getUserById(id);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, 1, "User could not be found!");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, 2, "The password is wrong!");
        }
        userDao.updatePassword(id, newPassword);
        return wrapperResponse(HttpStatus.OK, "OK.");
    }
}
