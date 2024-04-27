package com.airxiechao.y20.auth.pojo.exception;

public class InvalidAccessTokenException extends Exception {

    public static final String INVALID_ACCESS_TOKEN = "invalid access token";

    public InvalidAccessTokenException() {
        super(INVALID_ACCESS_TOKEN);
    }
}
