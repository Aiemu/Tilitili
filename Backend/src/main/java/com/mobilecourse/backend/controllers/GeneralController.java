package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.BackendApplication;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Random;

@RestController
@EnableAutoConfiguration
//@RequestMapping("")
public class GeneralController extends CommonController {

    private final Logger LOG = LoggerFactory.getLogger(BackendApplication.class);
    public class UserInfoCache {
        String username;
        String password;
        String email;
        String nickname;

        public UserInfoCache(String username, String password, String email, String nickname) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.nickname = nickname;
        }
    }

    @Autowired
    private UserDao userMapper;

    // 邮件部分
    private final HashMap<String, UserInfoCache> verifyCodesHashMap = new HashMap<>();
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailFromAddr;
//    private String urlFormat = "http://129.211.37.216:8080/verify/%s";
    private String urlFormat = "http://localhost:8080/verify/%s";

    /**
     * 用户注册, 会发送验证邮件
     * @param username
     * @param password
     * @param email
     * @param nickname
     * @return
     */
    @RequestMapping(value = "/signup", method = { RequestMethod.POST })
    public String signUp(@RequestParam(value = "username") String username,
                         @RequestParam(value = "password") String password,
                         @RequestParam(value = "email") String email,
                         @RequestParam(value = "nickname") String nickname) {
        //检查用户名是否重复
        User existUser = userMapper.getUserByUsername(username);
        if (existUser != null) {
            return wrapperMsg(400, "The user existed!");
        } else if (existUser.getEmail().equals(email)) {
            return wrapperMsg(400, "The email existed!");
        }

        //随机生成验证码
        String verifyCode = String.valueOf((new Random()).nextInt(10000000));
        while (verifyCodesHashMap.containsKey(verifyCode)) {
            verifyCode = String.valueOf((new Random()).nextInt(10000000));
        }

        //向该邮箱发送验证邮件.
        SimpleMailMessage verifyMailMessage = new SimpleMailMessage();
        verifyMailMessage.setFrom(mailFromAddr);
        verifyMailMessage.setTo(email);
        verifyMailMessage.setSubject("Tilitili注册验证");
        verifyMailMessage.setText("请点击以下链接: \n" +
                String.format(urlFormat, verifyCode) +
                "\n激活账号.");
        mailSender.send(verifyMailMessage);

        // 成功发送邮件, 将验证信息加入到缓存中, 返回200.
        verifyCodesHashMap.put(verifyCode, new UserInfoCache(username, password, email, nickname));
        LOG.info("The verification mail sends successfully.");
        return wrapperMsg(200, "success");
    }

    @RequestMapping(value = "/paramtest", method = { RequestMethod.POST })
    public String paramTest(HttpSession session) {
        LOG.info(session.getId());
        return wrapperMsg(200, "success");
    }

    /**
     * 用户注册激活验证, 通过邮件的url验证.
     * @return
     */
    @RequestMapping(value = "/verify/{code}", method = { RequestMethod.GET })
    public String verifyUser(@PathVariable String code) {
        if (verifyCodesHashMap.containsKey(code)) {
            //获取验证码对应的用户信息, 然后加入到数据库中
            UserInfoCache cache = verifyCodesHashMap.get(code);
            verifyCodesHashMap.remove(code);
            User user = new User();
            user.setUsername(cache.username);
            user.setPassword(cache.password);
            user.setEmail(cache.email);
            user.setNickname(cache.nickname);
            userMapper.registerUser(user);
            return wrapperMsg(200, "success");
        } else {
            return wrapperMsg(404, "user not found");
        }
    }

    @RequestMapping(value = "/logout", method = { RequestMethod.POST })
    public String logoutUser(HttpSession session) {
        session.invalidate();
        return wrapperMsg(200, "logout successfully.");
    }


}
