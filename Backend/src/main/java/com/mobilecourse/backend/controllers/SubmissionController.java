package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.annotation.LoginAuth;
import com.mobilecourse.backend.dao.*;
import com.mobilecourse.backend.exception.BusinessException;
import com.mobilecourse.backend.model.*;
import com.mobilecourse.backend.util.LoginConfig;
import jdk.nashorn.internal.scripts.JO;
import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger LOG = LoggerFactory.getLogger(SubmissionController.class);

    @RequestMapping(value = "/search", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> searchSubmission(@RequestParam(value = "title") String title,
                                                       @RequestParam(value = "maxCount", defaultValue = "10") Integer maxCount,
                                                       HttpSession session) {
        List<Submission> submissions = submissionDao.searchSubmission(title, maxCount);
        ArrayList<JSONObject> submissionJSONs = new ArrayList<>();
        Integer loginUid = (Integer) session.getAttribute("uid");
        for (Submission s: submissions) {
            submissionJSONs.add(wrapSubmission(s, loginUid));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("submission", submissionJSONs);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/upload", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> uploadSubmission(@RequestParam(value = "cover") String cover,
                                                       @RequestParam(value = "pid") Integer pid,
                                                       @RequestParam(value = "title") String title,
                                                       @RequestParam(value = "type") @Range(min = 0, max = 1) Integer type,
                                                       @RequestParam(value = "introduction") String introduction,
                                                       @RequestParam(value = "resource") String resource,
                                                       HttpSession session) {

        Integer uid = (Integer) session.getAttribute("uid");
        List<Integer> haveAuth = plateAuthDao.getSubmissionAuths(uid);
        if (!haveAuth.contains(pid)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, 1, "You have no authorization to submit in this plate.");
        }
        //可以投稿
        Submission submission = new Submission();
        submission.setUid(uid);
        submission.setTitle(title);
        submission.setPid(pid);
        submission.setCover(cover);
        submission.setType(type);
        submission.setIntroduction(introduction);
        submission.setResource(resource);
        submissionDao.putSubmission(submission);
        return wrapperResponse(HttpStatus.OK, "OK.");
    }

    @RequestMapping(value = "/get/{sid}", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getSubmission(@PathVariable Integer sid,
                                                    HttpSession session) {
        Submission submission = submissionDao.getSubmission(sid);
        if (submission == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, 1, "The submission could not be found.");
        }
        JSONObject jsonObject = wrapSubmission(submission, (Integer) session.getAttribute("uid"));
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "like/{sid}", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> likeSubmission(@RequestParam(value = "like") @Range(min = 0, max = 1) Integer like,
                                                     @PathVariable Integer sid,
                                                     HttpSession session) {
        Integer uid = (Integer) session.getAttribute("uid");
        Likes isLike = likesDao.getLike(uid, sid);
        if (like == 0) {
            //取消点赞
            if (isLike != null) {
                likesDao.deleteLike(uid, sid);
            }
        } else {
            //点赞
            if (isLike == null) {
                likesDao.putLike(uid, sid);
            }
        }
        return wrapperResponse(HttpStatus.OK, "OK.");
    }


    @LoginAuth
    @RequestMapping(value = "/watch/{sid}", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> increaseWatchTimes(@PathVariable Integer sid,
                                                         HttpSession session) {
        submissionDao.increaseWatchTimes(sid);
        Integer uid = (Integer) session.getAttribute("uid");
        History history = historyDao.getHistory(uid, sid);
        if (history == null) {
            historyDao.putHistory(uid, sid);
        } else {
            historyDao.updateHistory(uid, sid);
        }

        return wrapperResponse(HttpStatus.OK, "OK.");
    }

    @LoginAuth
    @RequestMapping(value = "/upload_history", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getUploadHistory(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                       @RequestParam(value = "count", defaultValue = "10") @Min(1) Integer count,
                                                       HttpSession session) {
        Integer uid = (Integer) session.getAttribute("uid");
        ArrayList<Integer> historyUids = new ArrayList<>();
        historyUids.add(uid);
        List<Submission> submissions = submissionDao.getSubmissionHistory(page * count, count, historyUids);
        Integer submissionCounts = submissionDao.getCountOfUser(historyUids);

        //装载投稿
        ArrayList<JSONObject> list = new ArrayList<>();
        for (Submission s: submissions) {
            list.add(wrapSubmission(s, uid));
        }
        JSONObject jsonObject = wrapPageFormat(list, page, count, submissionCounts);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/history", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getHistory(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                 @RequestParam(value = "count", defaultValue = "10") @Min(1) Integer count,
                                                 HttpSession session) {
        Integer uid = (Integer) session.getAttribute("uid");
        List<History> histories = historyDao.getUserHistories(page * count, count, uid);
        Integer historyCounts = historyDao.getUserHistoriesCount(uid);

        //装载投稿
        ArrayList<JSONObject> list = new ArrayList<>();
        for (History h: histories) {
            Submission s = submissionDao.getSubmission(h.getSid());
            JSONObject obj = wrapSubmission(s, uid);
            obj.put("historyTime", h.getWatchTime().getTime());
            list.add(obj);
        }
        JSONObject jsonObject = wrapPageFormat(list, page, count, historyCounts);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }


    @RequestMapping(value = "/hot", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getHotSubmission(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                       @RequestParam(value = "count", defaultValue = "10") @Min(1) Integer count,
                                                       HttpSession session) {
        List<Submission> submissions = submissionDao.getSubmissionHotPage(page * count, count, null);
        Integer submissionCounts = submissionDao.getCount(null);
        Integer uid = (Integer) session.getAttribute("uid");
        //装载投稿
        ArrayList<JSONObject> list = new ArrayList<>();
        for (Submission s: submissions) {
            list.add(wrapSubmission(s, uid));
        }
        JSONObject jsonObject = wrapPageFormat(list, page, count, submissionCounts);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @RequestMapping(value = "/new", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getNewSubmission(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                       @RequestParam(value = "count", defaultValue = "10") @Min(1) Integer count,
                                                       HttpSession session) {

        List<Submission> submissions = submissionDao.getSubmissionNewPage(page * count, count, null);
        Integer submissionCounts = submissionDao.getCount(null);
        Integer uid = (Integer) session.getAttribute("uid");
        //装载投稿
        ArrayList<JSONObject> list = new ArrayList<>();
        for (Submission s: submissions) {
            list.add(wrapSubmission(s, uid));
        }
        JSONObject jsonObject = wrapPageFormat(list, page, count, submissionCounts);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/comment/publish/{sid}", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> publishComment(@PathVariable Integer sid,
                                                     @RequestParam(value = "content") String content,
                                                     HttpSession session) {
        Integer uid = (Integer) session.getAttribute("uid");
        Comment comment = new Comment();
        comment.setUid(uid);
        comment.setSid(sid);
        comment.setContent(content);
        commentDao.putComment(comment);

        return wrapperResponse(HttpStatus.OK, "OK.");
    }

    @RequestMapping(value = "/comment/get/{sid}", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getComments(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                  @RequestParam(value = "count", defaultValue = "10") @Min(1) Integer count,
                                                  @PathVariable Integer sid,
                                                  HttpSession session) {

        List<Comment> comments = commentDao.getCommentPage(page * count, count, sid);
        Integer commentCounts = commentDao.getCommentCounts(sid);
        //装载评论
        ArrayList<JSONObject> list = new ArrayList<>();
        for (Comment c: comments) {
            User user = commentDao.getCommentUser(c.getUid());
            list.add(wrapComment(c, user));
        }
        JSONObject jsonObject = wrapPageFormat(list, page, count, commentCounts);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

}
