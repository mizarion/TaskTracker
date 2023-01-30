package com.consist.taskboot.exeption;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<RequestInfo> defaultErrorHandler(HttpServletRequest req, Exception e) {
        return new ResponseEntity<>(RequestInfo.builder()
                .method(req.getMethod())
                .request(req.getContextPath() + req.getServletPath())
                .message(e.getMessage())
                .build(),
                HttpStatus.NOT_ACCEPTABLE);
    }
}

