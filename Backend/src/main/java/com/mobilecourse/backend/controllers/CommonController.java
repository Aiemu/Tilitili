package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.BackendApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class CommonController {
    // session半个小时无交互就会过期
    private static int MAXTIME = 1800;
    private final Logger LOG = LoggerFactory.getLogger(BackendApplication.class);

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
        body.put("msg", msg);
        return new ResponseEntity<>(body, code);
    }

    // 添加信息到session之中，此部分用途很广泛，比如可以通过session获取到对应的用户名或者用户ID，避免繁冗操作
    public void putInfoToSession(HttpServletRequest request, String keyName, Object info)
    {
        HttpSession session = request.getSession();
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
}
