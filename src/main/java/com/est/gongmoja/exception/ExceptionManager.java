package com.est.gongmoja.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionManager {
    @ExceptionHandler(CustomException.class)
    public void customExceptionHandler(CustomException e) {
        throw new CustomException(e.getErrorCode());
    }
}
