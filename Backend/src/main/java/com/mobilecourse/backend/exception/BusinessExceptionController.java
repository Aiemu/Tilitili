package com.mobilecourse.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BusinessExceptionController extends BasicErrorController {

    private final Logger LOG = LoggerFactory.getLogger(BusinessExceptionController.class);
    public BusinessExceptionController(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }
    /**
     * 覆盖默认的Json响应
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus status = getStatus(request);

        LOG.error(String.format("Exception in %s", body.get("path")));
        LOG.error(String.format("timestamp: %s", body.get("timestamp")));
        LOG.error(String.format("message: %s", body.get("message")));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("timestamp", body.get("timestamp"));
        map.put("errorCode", 0);
        map.put("errorMessage", body.get("message"));
        map.put("uri", body.get("path"));
        return new ResponseEntity<Map<String, Object>>(map, status);
    }
}
