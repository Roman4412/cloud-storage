package com.pustovalov.cloudstorage.exception;

public class MinioOperationException extends RuntimeException {

    public MinioOperationException() {
    }

    public MinioOperationException(String message) {
        super(message);
    }

    public MinioOperationException(Throwable cause) {
        super(cause);
    }

    public MinioOperationException(String message,
                                   Throwable cause,
                                   boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MinioOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
