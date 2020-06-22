package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.annotation.LoginAuth;
import com.mobilecourse.backend.dao.FollowDao;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.exception.BusinessException;
import com.mobilecourse.backend.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@EnableAutoConfiguration
@Validated
@RequestMapping("/user")
public class UserController extends CommonController {

    private final Logger LOG = LoggerFactory.getLogger(UserController.class);

    // 重置密码部分
    public class UserInfoCache {
        String username;
        String password;

        public UserInfoCache(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailFromAddr;
    private final HashMap<String, UserInfoCache> verifyCodesHashMap = new HashMap<>();


    /**
     * 查看注册用户的个数.
     *
     * @return
     */
    @RequestMapping(value = "/stat")
    public ResponseEntity<JSONObject> getUserCount() {
        return wrapperResponse(HttpStatus.OK, "Tilitili当前一共有" + userDao.userCount() + "名用户.");
    }


    @RequestMapping(value = "/search", method = {RequestMethod.POST})
    public ResponseEntity<JSONObject> userSearch(@RequestParam(value = "username") String subUsername,
                                                 @RequestParam(value = "maxCount", defaultValue = "10") Integer maxCount,
                                                 HttpSession session) {
        List<User> matchedUsers = userDao.searchUser(subUsername, maxCount);
        JSONObject jsonObject = new JSONObject();
        JSONObject userJSON;
        ArrayList<JSONObject> userShorts = new ArrayList<>();
        Integer uid = (Integer) session.getAttribute("uid"); //试图获取当前登录用户uid

        for (User u : matchedUsers) {
            userJSON = new JSONObject();
            userJSON.put("uid", u.getUid());
            userJSON.put("nickname", u.getNickname());
            userJSON.put("avatar", u.getAvatar());
            userJSON.put("isFollowing", 0);
            if (uid != null && followDao.getFollow(uid, u.getUid()) != null) {
                userJSON.put("isFollowing", 1);
            }
            userShorts.add(userJSON);
        }
        jsonObject.put("users", userShorts);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/follow", method = {RequestMethod.POST})
    public ResponseEntity<JSONObject> followUser(@RequestParam(value = "uid") Integer followedUid,
                                                 HttpSession session) {
        Integer followerUid = (Integer) session.getAttribute("uid");
        Follow isFollow = followDao.getFollow(followerUid, followedUid);
        if (isFollow == null) {
            followDao.putFollow(followerUid, followedUid);
        }
        return wrapperResponse(HttpStatus.OK, "OK.");
    }

    @LoginAuth
    @RequestMapping(value = "/unfollow", method = {RequestMethod.POST})
    public ResponseEntity<JSONObject> unfollowUser(@RequestParam(value = "uid") Integer followedUid,
                                                   HttpSession session) {
        Integer followerUid = (Integer) session.getAttribute("uid");
        Follow isFollow = followDao.getFollow(followerUid, followedUid);
        if (isFollow != null) {
            followDao.deleteFollow(followerUid, followedUid);
        }
        return wrapperResponse(HttpStatus.OK, "OK.");
    }


    @RequestMapping(value = "/profile/info/{uid}", method = {RequestMethod.GET})
    public ResponseEntity<JSONObject> getInfo(@PathVariable(value = "uid") Integer uid) {
        User user = userDao.getUserByUid(uid);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, 1, "The user does not exist.");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", user.getUsername());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("nickname", user.getNickname());
        jsonObject.put("department", user.getDepartment());
        jsonObject.put("joinAt", user.getJoinAt().getTime());
        jsonObject.put("bio", user.getBio());
        jsonObject.put("avatar", user.getAvatar());
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/profile/edit", method = {RequestMethod.POST})
    public ResponseEntity<JSONObject> editInfo(@RequestParam(value = "nickname", required = false) String nickname,
                                               @RequestParam(value = "bio", required = false) String bio,
                                               @RequestParam(value = "department", required = false) String department,
                                               @RequestParam(value = "avatar", required = false) String avatar,
                                               HttpSession session) {
        Integer uid = (Integer) session.getAttribute("uid");
        User updateUser = new User();
        updateUser.setUid(uid);
        updateUser.setNickname(nickname);
        updateUser.setDepartment(department);
        updateUser.setBio(bio);
        updateUser.setAvatar(avatar);
        userDao.updateUser(updateUser);
        return wrapperResponse(HttpStatus.OK, new JSONObject());
    }

    @RequestMapping(value = "/password/modify", method = {RequestMethod.POST})
    public ResponseEntity<JSONObject> updatePassword(@RequestParam(value = "uid") Integer uid,
                                                     @RequestParam(value = "old") String oldPassword,
                                                     @RequestParam(value = "new") String newPassword) {
        User user = userDao.getUserByUid(uid);
        oldPassword = encryptBasedDes(oldPassword);
        newPassword = encryptBasedDes(newPassword);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, 1, "User could not be found!");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, 2, "The password is wrong!");
        }
        userDao.updatePassword(user.getUsername(), newPassword);
        return wrapperResponse(HttpStatus.OK, "OK.");
    }

    @RequestMapping(value = "/password/forget", method = {RequestMethod.POST})
    public ResponseEntity<JSONObject> forgetPassword(@RequestParam(value = "username") String username,
                                                     @RequestParam(value = "password") String password) {
        User user = userDao.getUserByUsername(username);
        password = encryptBasedDes(password);
        if (user == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, 1, "The username does not exist in the Database.");
        }
        //生成验证码
        String verifyCode = UUID.randomUUID().toString();

        //向该邮箱发送验证邮件.
        String urlFormat = "http://129.211.37.216:8888/user/password/verify/%s";
        SimpleMailMessage verifyMailMessage = new SimpleMailMessage();
        verifyMailMessage.setFrom(mailFromAddr);
        verifyMailMessage.setTo(user.getEmail());
        verifyMailMessage.setSubject("Tilitili账号重置密码验证");
        verifyMailMessage.setText("请点击以下链接: \n" +
                String.format(urlFormat, verifyCode) +
                "\n确认重置密码.");
        mailSender.send(verifyMailMessage);
        verifyCodesHashMap.put(verifyCode, new UserInfoCache(username, password));
        LOG.info("The verification mail sends successfully.");
        return wrapperResponse(HttpStatus.OK, "success");
    }

    @RequestMapping(value = "/password/verify/{code}", method = {RequestMethod.GET})
    public ResponseEntity<JSONObject> resetPassword(@PathVariable String code) {
        if (verifyCodesHashMap.containsKey(code)) {
            //获取验证码对应的用户信息, 然后加入到数据库中
            UserInfoCache cache = verifyCodesHashMap.get(code);
            verifyCodesHashMap.remove(code);
            userDao.updatePassword(cache.username, cache.password);
            LOG.warn(String.format("The verifyCode (%s) is verified successfully.", code));
            return wrapperResponse(HttpStatus.OK, "success");
        } else {
            LOG.warn(String.format("The verifyCode (%s) does not exist.", code));
            throw new BusinessException(HttpStatus.NOT_FOUND, 1, "The verification code could not be found!");
        }
    }

    @LoginAuth
    @RequestMapping(value = "/activity", method = {RequestMethod.GET})
    public ResponseEntity<JSONObject> getActivity(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                  @RequestParam(value = "count", defaultValue = "10") @Min(1) Integer count,
                                                  HttpSession session) {
        Integer uid = (Integer) session.getAttribute("uid");
        List<Integer> historyUids = followDao.getFolloweds(uid);
        List<Submission> submissions;
        Integer submissionCounts = 0;
        if (historyUids.size() != 0) {
            submissions = submissionDao.getSubmissionHistory(page * count, count, historyUids);
            submissionCounts = submissionDao.getCountOfUser(historyUids);
        } else submissions = new ArrayList<>();

        //装载投稿
        ArrayList<JSONObject> list = new ArrayList<>();
        for (Submission s : submissions) {
            list.add(wrapSubmission(s, uid));
        }
        JSONObject jsonObject = wrapPageFormat(list, page, count, submissionCounts);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/favorite", method = {RequestMethod.GET})
    public ResponseEntity<JSONObject> getFavorite(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                  @RequestParam(value = "count", defaultValue = "10") @Min(1) Integer count,
                                                  HttpSession session) {
        Integer uid = (Integer) session.getAttribute("uid");
        List<Integer> favoriteSids = favoriteDao.getUserAllFavorite(page * count, count, uid);
        Integer submissionCounts = favoriteDao.getUserAllFavoriteCount(uid);
        List<Submission> submissions = new ArrayList<>();
        for (Integer sid: favoriteSids) {
            submissions.add(submissionDao.getSubmission(sid));
        }
        //装载投稿
        ArrayList<JSONObject> list = new ArrayList<>();
        for (Submission s : submissions) {
            list.add(wrapSubmission(s, uid));
        }
        JSONObject jsonObject = wrapPageFormat(list, page, count, submissionCounts);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/friend", method = {RequestMethod.GET})
    public ResponseEntity<JSONObject> getAllFriends(HttpSession session) {
        Integer uid = (Integer) session.getAttribute("uid");
        List<Integer> followedUids = followDao.getFolloweds(uid);
        ArrayList<JSONObject> followedUsers = new ArrayList<>();
        for (Integer followedUid : followedUids) {
            User followedUser = userDao.getUserByUid(followedUid);
            JSONObject userJSON = new JSONObject();
            userJSON.put("uid", followedUser.getUid());
            userJSON.put("nickname", followedUser.getNickname());
            userJSON.put("avatar", followedUser.getAvatar());
            followedUsers.add(userJSON);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("users", followedUsers);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/message", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getOfflineMessages(HttpSession session) {
        Integer uid = (Integer) session.getAttribute("uid");
        // 获取所有的离线信息
        List<Message> messages = messageDao.getOfflineMessages(uid);
        messageDao.clearOfflineMessages(uid);
        ArrayList<JSONObject> messageList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        for (Message m: messages) {
            JSONObject messageObject = new JSONObject();
            User srcUser = userDao.getUserByUid(m.getSrcUid());
            messageObject.put("mid", m.getMid());
            messageObject.put("uid", m.getSrcUid());
            messageObject.put("avatar", srcUser.getAvatar());
            messageObject.put("nickname", srcUser.getNickname());
            messageObject.put("content", m.getContent());
            messageObject.put("messageTime", m.getMessageTime().getTime());
            messageList.add(messageObject);
        }
        jsonObject.put("messages", messageList);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/send_message", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> sendMessage(@RequestParam(value = "uid") Integer destUid,
                                                  @RequestParam(value = "content") String content,
                                                  HttpSession session) {
        Integer srcUid = (Integer) session.getAttribute("uid");
        Message message = new Message();
        message.setSrcUid(srcUid);
        message.setDestUid(destUid);
        message.setType(0);
        message.setContent(content);
        messageDao.putMessage(message);

        return wrapperResponse(HttpStatus.OK, "OK.");
    }
}
