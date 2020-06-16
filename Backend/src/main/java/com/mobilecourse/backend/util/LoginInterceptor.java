package com.mobilecourse.backend.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.BackendApplication;
import com.mobilecourse.backend.annotation.LoginAuth;
import com.mobilecourse.backend.controllers.CommonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;

public class LoginInterceptor implements HandlerInterceptor {
    public static final String LOGIN_KEY = "LOGIN";
    private final Logger LOG = LoggerFactory.getLogger(BackendApplication.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if (method.isAnnotationPresent(LoginAuth.class)) {
            HttpSession session = request.getSession();
            Object loginStatus = session.getAttribute(LOGIN_KEY);
            if (loginStatus == null) {
                // 未登录
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("timestamp", new Date());
                jsonObject.put("errorCode", 1);
                jsonObject.put("errorMessage", "Need Login.");
                jsonObject.put("uri", request.getRequestURI());
                response.getWriter().append(jsonObject.toJSONString());
                LOG.warn("A user try to use API without login authorization");
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
