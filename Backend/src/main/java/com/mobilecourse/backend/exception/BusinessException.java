package com.mobilecourse.backend.exception;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private HttpStatus status;
    private Integer code;
    private String msg;

    public HttpStatus getStatus() {
        return status;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public BusinessException(HttpStatus status, Integer code, String msg) {
        this.status = status;
        this.code = code;
        this.msg = msg;
    }
}
