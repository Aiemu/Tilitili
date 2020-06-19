package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.annotation.LoginAuth;
import com.mobilecourse.backend.dao.*;
import com.mobilecourse.backend.exception.BusinessException;
import com.mobilecourse.backend.model.*;
import com.mobilecourse.backend.util.LoginConfig;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@RestController
@EnableAutoConfiguration
@Validated
@RequestMapping("/submission")
public class SubmissionController extends CommonController {

    @Autowired
    private SubmissionDao submissionDao;
    @Autowired
    private PlateDao plateDao;
    @Autowired
    private PlateAuthDao plateAuthDao;
    @Autowired
    private LikesDao likesDao;
    @Autowired
    private CommentDao commentDao;

    public JSONObject wrapComment(Comment comment) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid", comment.getUid());
        jsonObject.put("content", comment.getContent());
        jsonObject.put("commentTime", comment.getCommentTime().getTime());
        return jsonObject;
    }

    public JSONObject wrapSubmission(Submission submission, Integer uid) {
        JSONObject jsonObject = new JSONObject();
        Plate plate = plateDao.getPlate(submission.getPid());

        jsonObject.put("type", submission.getType());
        jsonObject.put("plateTitle", plate.getTitle());
        jsonObject.put("title", submission.getTitle());
        jsonObject.put("cover", submission.getCover());
        jsonObject.put("introduction", submission.getIntroduction());
        jsonObject.put("resource", submission.getResource());
        jsonObject.put("submissionTime", submission.getSubmissionTime().getTime());
        jsonObject.put("watchTimes", submission.getWatchTimes());
        jsonObject.put("likesCount", likesDao.getSubmissionLikes(submission.getSid()));
        //没有登录默认不点赞
        jsonObject.put("isLike", 0);
        if (uid != null) {
            if (likesDao.getLike(uid, submission.getSid()) != null) {
                jsonObject.put("isLike", 1);
            }
        }
        jsonObject.put("commentsCount", commentDao.getCommentCounts(submission.getSid()));
        return jsonObject;
    }

    @RequestMapping(value = "/upload", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> uploadSubmission() {
        return wrapperResponse(HttpStatus.NOT_IMPLEMENTED, "NOT SUPPOSED.");
    }

    @RequestMapping(value = "/watch/{sid}", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> increaseWatchTimes(@PathVariable Integer sid) {
        submissionDao.increaseWatchTimes(sid);
        return wrapperResponse(HttpStatus.OK, "OK.");
    }

    @RequestMapping(value = "/get/{sid}", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getSubmission(@PathVariable Integer sid,
                                                    HttpSession session) {
        Submission submission = submissionDao.getSubmission(sid);
        Integer uid = null;
        if (submission == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, 1, "The submission could not be found.");
        }
        if (session.getAttribute(LoginConfig.LOGIN_KEY) != null) {
            uid = (Integer) session.getAttribute("id");
        }
        JSONObject jsonObject = wrapSubmission(submission, uid);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @RequestMapping(value = "/hot", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getHotSubmission(@RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
                                                       @RequestParam(value = "count", defaultValue = "10") @Min(1) Integer count,
                                                       HttpSession session) {
        JSONObject jsonObject = new JSONObject();
        List<Submission> submissions = submissionDao.getSubmissionHotPage((page - 1) * count, count, 0);
        Integer submissionCounts = submissionDao.getCount(0);
        jsonObject.put("currentPage", page);
        jsonObject.put("pageSize", submissions.size());
        jsonObject.put("totalPage", Math.ceil(submissionCounts / count));
        jsonObject.put("totalCount", submissionCounts);

        Integer uid = null;
        if (session.getAttribute(LoginConfig.LOGIN_KEY) != null) {
            uid = (Integer) session.getAttribute("id");
        }
        //装载投稿
        ArrayList<JSONObject> list = new ArrayList<>();
        for (Submission s: submissions) {
            list.add(wrapSubmission(s, uid));
        }
        jsonObject.put("list", list);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "like/{sid}", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> likeSubmission(@RequestParam(value = "like") @Range(min = 0, max = 1) Integer like,
                                                     @PathVariable Integer sid,
                                                     HttpSession session) {
        Integer uid = (Integer) session.getAttribute("id");
        Likes isLike = likesDao.getLike(uid, sid);
        if (like == 0) {
            //取消点赞
            if (isLike != null) {
                likesDao.deleteLike(uid, sid);
            }
        } else {
            //点赞
            if (isLike != null) {
                likesDao.putLike(uid, sid);
            }
        }
        return wrapperResponse(HttpStatus.OK, "OK.");
    }

    @RequestMapping(value = "comment/{sid}", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getComments(@RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
                                                  @RequestParam(value = "count", defaultValue = "10") @Min(1) Integer count,
                                                  @PathVariable Integer sid,
                                                  HttpSession session) {
        List<Comment> comments = commentDao.getCommentPage((page - 1) * count, count, sid);

    }
}
