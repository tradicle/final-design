package com.xxx.animal.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Result<T>(int code, String message, T data) {
    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "OK", data);
    }

    public static Result<Void> ok() {
        return new Result<>(0, "OK", null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(-1, message, null);
    }
}
