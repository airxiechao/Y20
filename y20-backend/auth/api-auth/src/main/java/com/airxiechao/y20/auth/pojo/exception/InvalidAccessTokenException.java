package com.airxiechao.y20.auth.pojo.exception;

public class InvalidAccessTokenException extends Exception {

    public InvalidAccessTokenException() {
        super("invalid access token");
    }
}
