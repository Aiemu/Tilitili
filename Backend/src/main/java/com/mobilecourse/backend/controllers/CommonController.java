package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.BackendApplication;
import com.mobilecourse.backend.dao.*;
import com.mobilecourse.backend.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@EnableAutoConfiguration
public class CommonController {

    @Autowired
    protected SubmissionDao submissionDao;
    @Autowired
    protected PlateDao plateDao;
    @Autowired
    protected PlateAuthDao plateAuthDao;
    @Autowired
    protected LikesDao likesDao;
    @Autowired
    protected CommentDao commentDao;
    @Autowired
    protected FollowDao followDao;
    @Autowired
    protected UserDao userDao;
    @Autowired
    protected HistoryDao historyDao;

    // session半个小时无交互就会过期
    private static int MAXTIME = 1800;
    private final Logger LOG = LoggerFactory.getLogger(CommonController.class);

    // 添加一个code，方便客户端根据code来判断服务器处理状态并解析对应的msg
    String wrapperMsg(int code, String msg) {
        JSONObject wrapperMsg = new JSONObject();
        wrapperMsg.put("code", code);
        wrapperMsg.put("msg", msg);
        return wrapperMsg.toJSONString();
    }

    ResponseEntity<JSONObject> wrapperResponse(HttpStatus code, JSONObject body) {
        return new ResponseEntity<>(body, code);
    }

    ResponseEntity<JSONObject> wrapperResponse(HttpStatus code, String msg) {
        JSONObject body = new JSONObject();
        body.put("message", msg);
        return new ResponseEntity<>(body, code);
    }

    // 添加信息到session之中，此部分用途很广泛，比如可以通过session获取到对应的用户名或者用户ID，避免繁冗操作
    public void putInfoToSession(HttpSession session, String keyName, Object info)
    {
        //设置session过期时间，单位为秒(s)
        session.setMaxInactiveInterval(MAXTIME);
        //将信息存入session
        session.setAttribute(keyName, info);
    }
    public void logOutError(Exception e) {
        LOG.error(e.getClass().toString());
        for (StackTraceElement ste: e.getStackTrace()) {
            LOG.error(ste.toString());
        }
    }

    // 添加信息到session之中，此部分用途很广泛，比如可以通过session获取到对应的用户名或者用户ID，避免繁冗操作
    public void removeInfoFromSession(HttpServletRequest request, String keyName)
    {
        HttpSession session = request.getSession();
        // 删除session里面存储的信息，一般在登出的时候使用
        session.removeAttribute(keyName);
    }

    // 评论包装
    public JSONObject wrapComment(Comment comment, User user) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nickname", user.getNickname());
        jsonObject.put("avatar", user.getAvatar());
        jsonObject.put("content", comment.getContent());
        jsonObject.put("commentTime", comment.getCommentTime().getTime());
        return jsonObject;
    }

    // 投稿包装
    public JSONObject wrapSubmission(Submission submission, Integer loginUserUid) {
        JSONObject jsonObject = new JSONObject();
        User submissionUser = userDao.getUserByUid(submission.getUid());
        Plate plate = plateDao.getPlate(submission.getPid());

        jsonObject.put("sid", submission.getSid());
        jsonObject.put("type", submission.getType());
        jsonObject.put("plateTitle", plate.getTitle());
        jsonObject.put("title", submission.getTitle());
        jsonObject.put("cover", submission.getCover());
        jsonObject.put("introduction", submission.getIntroduction());
        jsonObject.put("resource", submission.getResource());
        jsonObject.put("submissionTime", submission.getSubmissionTime().getTime());
        jsonObject.put("watchTimes", submission.getWatchTimes());
        jsonObject.put("likesCount", likesDao.getSubmissionLikes(submission.getSid()));
        //没有登录默认不点赞, 默认不关注
        jsonObject.put("isLike", 0);
        jsonObject.put("following", 0);
        if (loginUserUid != null) {
            Likes isLike = likesDao.getLike(loginUserUid, submission.getSid());
            if (isLike != null) {
                jsonObject.put("isLike", 1);
            }
            Follow follow = followDao.getFollow(loginUserUid, submissionUser.getUid());
            if (follow != null) {
                jsonObject.put("following", 1);
            }
        }
        jsonObject.put("commentsCount", commentDao.getCommentCounts(submission.getSid()));
        jsonObject.put("uid", submissionUser.getUid());
        jsonObject.put("userNickname", submissionUser.getNickname());

        return jsonObject;
    }

    // 板块信息包装
    public JSONObject wrapPlate(Plate plate) {
        JSONObject plateJSON = new JSONObject();
        plateJSON.put("pid", plate.getPid());
        plateJSON.put("title", plate.getTitle());
        plateJSON.put("description", plate.getDescription());
        plateJSON.put("cover", plate.getCover());
        return plateJSON;
    }

    // 分页式格式包装.
    public JSONObject wrapPageFormat(List<JSONObject> list,
                                     Integer currentPage, Integer size, Integer totalCount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currentPage", currentPage);
        jsonObject.put("pageSize", list.size());
        jsonObject.put("totalPage", new Double(Math.ceil(totalCount / size)).intValue());
        jsonObject.put("totalCount", totalCount);
        jsonObject.put("list", list);
        return jsonObject;
    }
}
