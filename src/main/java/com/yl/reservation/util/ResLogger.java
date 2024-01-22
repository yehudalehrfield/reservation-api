package com.yl.reservation.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Data
public class ResLogger {

//    @Autowired
//    private HttpServletRequest reservationRequest;

    private static final Logger logger = LoggerFactory.getLogger(ResLogger.class);

    private HttpMethod requestMethod; //todo: remove this?
    private String query;
    private String requestUrl;
    private String requestBody;
    private Map<String, String> requestHeaders;
    private long startTime;
    private long endTime;
    private long timeTakenInMS;
    private HttpStatus responseStatus;
    private String responseBody;

    public ResLogger(long startTime, HttpMethod method, String query) {
        this.startTime = startTime;
        this.requestMethod = method;
        this.query = query;
    }

    public void log(){
        if (responseStatus.is2xxSuccessful()){
            logger.info(this.toString());
        } else {
            logger.error(this.toString());
        }
    }

    public void getHeaders(HttpServletRequest reservationRequest) {

        Map<String, String> headerMap = new HashMap<>();

        Enumeration<String> headerNames = reservationRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = reservationRequest.getHeader(key);
            headerMap.put(key, value);
        }

        this.setRequestHeaders(headerMap);
    }

    public void setValuesToLogger(HttpStatus responseStatus, String responseBody){
        this.setEndTime(System.currentTimeMillis());

        this.setResponseStatus(responseStatus);
        this.setResponseBody(responseBody);
        this.setTimeTakenInMS(this.endTime - this.startTime);

        this.log();
    }
}
