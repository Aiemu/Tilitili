package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.annotation.LoginAuth;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.exception.BusinessException;
import com.mobilecourse.backend.model.User;
import com.mobilecourse.backend.util.LoginConfig;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

@RestController
@EnableAutoConfiguration
//@RequestMapping("")
@Validated
public class GeneralController extends CommonController {

    private final Logger LOG = LoggerFactory.getLogger(GeneralController.class);
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
    private String urlFormat = "http://129.211.37.216:8888/verify/%s";

    /**
     * 用户注册, 会发送验证邮件
     * @param username
     * @param password
     * @param email
     * @param nickname
     * @return
     */
    @RequestMapping(value = "/signup", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> signUp(@RequestParam(value = "username") @Length(max = 20) String username,
                                             @RequestParam(value = "password") @Length(max = 100) String password,
                                             @RequestParam(value = "email") @Length(max = 100) String email,
                                             @RequestParam(value = "nickname") @Length(max = 20) String nickname,
                                             HttpServletRequest request) {
        //检查用户名是否重复
        User existUser = userMapper.getUserByUsername(username);
        if (existUser != null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, 1,
                    String.format("The username (%s) in the register process has already existed in Database.", username));
        }
        //检查邮箱是否重复.
        User existEmail = userMapper.getUserByEmail(email);
        if (existEmail != null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, 1,
                    String.format("The email (%s) in the register process has already existed in Database.", email));
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
        return wrapperResponse(HttpStatus.OK, "success");
    }

    @RequestMapping(value = "/paramtest", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> paramTest(@RequestParam(value = "file") MultipartFile file) {
        return wrapperResponse(HttpStatus.OK, "success");
    }

    /**
     * 用户注册激活验证, 通过邮件的url验证.
     * @return
     */
    @RequestMapping(value = "/verify/{code}", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> verifyUser(@PathVariable String code) {
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
            LOG.warn(String.format("The verifyCode (%s) registered successfully.", code));
            return wrapperResponse(HttpStatus.OK, "success");
        } else {
            LOG.warn(String.format("The verifyCode (%s) has not registered yet.", code));
            throw new BusinessException(HttpStatus.NOT_FOUND, 1, "The verification code could not be found!");
        }
    }

    @RequestMapping(value = "/login", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> loginUser(@RequestParam(value = "username") String username,
                                                @RequestParam(value = "password") String password,
                                                HttpSession session) {
        User existUser = userMapper.getUserByUsername(username);
        JSONObject jsonObject = new JSONObject();
        if (existUser == null) {
            //用户不存在
            throw new BusinessException(HttpStatus.BAD_REQUEST, 1, "User could not be found.");
        }
        if (!existUser.getPassword().equals(password)) {
            //用户密码错误
            throw new BusinessException(HttpStatus.BAD_REQUEST, 1, "Password is wrong.");
        }
        // 登录成功, 存储登录状态
        putInfoToSession(session, LoginConfig.LOGIN_KEY, true);
        putInfoToSession(session, "id", existUser.getId());
        putInfoToSession(session, "username", existUser.getUsername());
        putInfoToSession(session, "nickname", existUser.getNickname());
        // 返回userShort
        jsonObject.put("id", existUser.getId());
        jsonObject.put("username", existUser.getUsername());
        jsonObject.put("nickname", existUser.getNickname());

        LOG.info(String.format("The user (%s) login.", existUser.getUsername()));
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/logout", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> logoutUser(HttpSession session) {
        LOG.info(String.format("The user (%s) logouts.", session.getAttribute("username")));
        session.invalidate();
        return wrapperResponse(HttpStatus.OK, "logout successfully.");
    }

    @LoginAuth
    @RequestMapping(value = "/whoami", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> whoAmI(HttpSession session) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", session.getAttribute("id"));
        jsonObject.put("username", session.getAttribute("username"));
        jsonObject.put("nickname", session.getAttribute("nickname"));
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @RequestMapping(value = "/user_search", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> userSearch(@RequestParam(value = "username") String subUsername) {
        List<User> matchedUsers = userMapper.searchUser(subUsername, 5);
        JSONObject jsonObject = new JSONObject();
        JSONObject userJSON;
        ArrayList<JSONObject> userShorts = new ArrayList<>();
        for (User u: matchedUsers) {
            userJSON = new JSONObject();
            userJSON.put("id", u.getId());
            userJSON.put("username", u.getUsername());
            userJSON.put("nickname", u.getNickname());
            userShorts.add(userJSON);
        }
        jsonObject.put("userShorts", userShorts);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/upload", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> uploadFile(@RequestParam(value = "file") MultipartFile file,
                                                 @RequestParam(value = "type") @Range(min = 0, max = 2) Integer type) {

        //获取唯一文件名
        String fileName = file.getOriginalFilename();
        String extName = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : null;
        String uuid = UUID.randomUUID().toString();
        String newName = uuid + extName;

        File toSaveFile;
        String resourceURI;

        if (type == 0) {
            //图片
            toSaveFile = new File(ClassUtils.getDefaultClassLoader().getResource("static/image").getPath(), newName);
            resourceURI = "/static/image/" + newName;
        } else if (type == 1) {
            //html
            toSaveFile = new File(ClassUtils.getDefaultClassLoader().getResource("static/html").getPath(), newName);
            resourceURI = "/static/html/" + newName;
        } else {
            //视频类型.
            toSaveFile = new File(ClassUtils.getDefaultClassLoader().getResource("static/video").getPath(), newName);
            resourceURI = "/static/video/" + newName;
        }
        try {
            // 保存文件并返回相应的uri.
            file.transferTo(toSaveFile);
            LOG.info(String.format("File (%s) is uploaded successfully and renamed to (%s)",
                    file.getOriginalFilename(), newName));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uri", resourceURI);
            return wrapperResponse(HttpStatus.OK, jsonObject);
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, 1,
                    String.format("File (%s) failed to be uploaded", file.getOriginalFilename()));
        }
    }
}
