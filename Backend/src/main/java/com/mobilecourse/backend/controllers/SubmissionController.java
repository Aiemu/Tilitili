package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.SubmissionDao;
import com.mobilecourse.backend.exception.BusinessException;
import com.mobilecourse.backend.model.Submission;
import com.mobilecourse.backend.util.LoginConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@EnableAutoConfiguration
@RequestMapping("/submission")
public class SubmissionController extends CommonController {

    @Autowired
    private SubmissionDao submissionDao;

    @RequestMapping(value = "/upload", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> uploadSubmission() {
        return wrapperResponse(HttpStatus.NOT_IMPLEMENTED, "NOT SUPPOSED.");
    }

    @RequestMapping(value = "/get/{sid}", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getSubmission(@PathVariable Integer sid,
                                                    HttpSession session) {
        JSONObject jsonObject = new JSONObject();
        Submission submission = submissionDao.getSubmission(sid);
        if (submission == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, 1, "The submission could not be found.");
        }


        if (session.getAttribute(LoginConfig.LOGIN_KEY) != null) {

        }

    }
}
