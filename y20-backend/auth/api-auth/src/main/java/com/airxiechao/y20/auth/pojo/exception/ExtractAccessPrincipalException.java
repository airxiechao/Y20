package com.airxiechao.y20.auth.pojo.exception;

public class ExtractAccessPrincipalException extends Exception {
    public ExtractAccessPrincipalException() {
    }

    public ExtractAccessPrincipalException(String message) {
        super(message);
    }

    public ExtractAccessPrincipalException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtractAccessPrincipalException(Throwable cause) {
        super(cause);
    }

    public ExtractAccessPrincipalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
