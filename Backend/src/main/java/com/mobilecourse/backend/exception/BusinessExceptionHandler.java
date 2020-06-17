package com.mobilecourse.backend.exception;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@ControllerAdvice
public class BusinessExceptionHandler {

    @Autowired
    private ErrorAttributes errorAttributes;
    private final Logger LOG = LoggerFactory.getLogger(BusinessExceptionHandler.class);

    /**
     * Tilitili后端错误处理
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseEntity<JSONObject> handleBusinessException(HttpServletRequest request, BusinessException e) {
        ServletWebRequest req = new ServletWebRequest(request);
        Map<String, Object> map = errorAttributes.getErrorAttributes(req, false);
        LOG.error(String.format("Exception in %s", request.getRequestURI()));
        LOG.error(String.format("timestamp: %s", map.get("timestamp")));
        LOG.error(String.format("message: %s", e.getMsg()));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timestamp", map.get("timestamp"));
        jsonObject.put("errorCode", e.getCode());
        jsonObject.put("errorMessage", e.getMsg());
        jsonObject.put("uri", request.getRequestURI());
        return new ResponseEntity<>(jsonObject, e.getStatus());
    }
}
