package com.yl.reservation.controller;

import com.yl.reservation.exception.ResException;
import com.yl.reservation.service.host.HostCreateUpdateResponse;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonRequestException(HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        ResException resException = new ResException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        // return new ResponseEntity<>(hostException, HttpStatus.BAD_REQUEST);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resException.getMessage());
        // return new ResponseEntity<>(exception.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResException.class)
    public ResponseEntity<HostCreateUpdateResponse> handleHostException(ResException exception) {
        HostCreateUpdateResponse response = new HostCreateUpdateResponse();
        response.setMessage(exception.getMessage());
        return new ResponseEntity<>(response, exception.getStatus());
    }
}
