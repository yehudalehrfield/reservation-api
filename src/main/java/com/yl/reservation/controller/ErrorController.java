package com.yl.reservation.controller;

import com.yl.reservation.exception.HostException;
import com.yl.reservation.service.HostUpdateResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonRequestException(HttpMessageNotReadableException exception, HttpServletRequest request) {
        HostException hostException = new HostException(HttpStatus.BAD_REQUEST, exception.getMessage());
//        return new ResponseEntity<>(hostException, HttpStatus.BAD_REQUEST);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(hostException.getMessage());
//        return new ResponseEntity<>(exception.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HostException.class)
    public ResponseEntity<HostUpdateResponse> handleHostException(HostException exception){
        HostUpdateResponse response = new HostUpdateResponse();
        response.setMessage(exception.getMessage());
        return new ResponseEntity<>(response,exception.getStatus());
    }
}
