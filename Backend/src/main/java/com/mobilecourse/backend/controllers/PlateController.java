package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.annotation.LoginAuth;
import com.mobilecourse.backend.dao.*;
import com.mobilecourse.backend.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jackson.JsonObjectDeserializer;
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
@RequestMapping("/plate")
public class PlateController extends CommonController {

    private final Logger LOG = LoggerFactory.getLogger(PlateController.class);

    @RequestMapping(value = "/getall", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getAllPlates() {
        List<Plate> plates = plateDao.getAllPlates();
        JSONObject jsonObject = new JSONObject();
        ArrayList<JSONObject> plateList = new ArrayList<>();
        for (Plate p: plates) {
            plateList.add(wrapPlate(p));
        }
        jsonObject.put("plates", plateList);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @LoginAuth
    @RequestMapping(value = "/getprivilege", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> getPrivilege(HttpSession session) {
        ArrayList<JSONObject> plateList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        Integer uid = (Integer) session.getAttribute("uid");
        List<Integer> authPlatePids = plateAuthDao.getSubmissionAuths(uid);
        for (Integer pid: authPlatePids) {
            plateList.add(wrapPlate(plateDao.getPlate(pid)));
        }
        jsonObject.put("plates", plateList);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }

    @RequestMapping(value = "/list/{pid}", method = { RequestMethod.GET })
    public ResponseEntity<JSONObject> listSubmissions(@PathVariable Integer pid,
                                                      @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                      @RequestParam(value = "count", defaultValue = "10") @Min(1) Integer count,
                                                      HttpSession session) {
        List<Submission> submissions = submissionDao.getSubmissionNewPage(page * count, count, pid);
        Integer submissionCounts = submissionDao.getCount(pid);
        Integer uid = (Integer) session.getAttribute("uid");
        //装载投稿
        ArrayList<JSONObject> list = new ArrayList<>();
        for (Submission s: submissions) {
            list.add(wrapSubmission(s, uid));
        }
        JSONObject jsonObject = wrapPageFormat(list, page, count, submissionCounts);
        return wrapperResponse(HttpStatus.OK, jsonObject);
    }
}
