package com.yl.reservation.host.controller;

import com.yl.reservation.host.exception.HostException;
import com.yl.reservation.host.service.HostResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HostException> handleJsonRequestException(HttpMessageNotReadableException exception, HttpServletRequest request) {
        HostException hostException = new HostException(HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(hostException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HostException.class)
    public ResponseEntity<HostResponse> handleHostException(HostException exception){
        HostResponse response = new HostResponse();
        response.setMessage(exception.getMessage());
        return new ResponseEntity<>(response,exception.getStatus());
    }
}
