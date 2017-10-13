package com.kr4ken.habitica.exception;

public class HabiticaHttpException extends RuntimeException {

    public HabiticaHttpException() {
    }

    public HabiticaHttpException(String message) {
        super(message);
    }

    public HabiticaHttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HabiticaHttpException(Throwable cause) {
        super(cause);
    }

    public HabiticaHttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
