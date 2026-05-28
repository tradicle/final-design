package com.xxx.animal.config;

import com.xxx.animal.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = "服务器内部错误";
        }
        return Result.fail(message);
    }
}
