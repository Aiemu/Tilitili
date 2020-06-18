package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping("/submission")
public class SubmissionController extends CommonController {

    @RequestMapping(value = "/upload", method = { RequestMethod.POST })
    public ResponseEntity<JSONObject> uploadSubmission() {
        return wrapperResponse(HttpStatus.NOT_IMPLEMENTED, "NOT SUPPOSED.");
    }
}
