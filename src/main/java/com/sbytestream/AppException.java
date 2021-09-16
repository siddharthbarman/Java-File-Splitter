package com.sbytestream;

public class AppException extends Exception {
    public AppException() {
        super();
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(Exception innerException) {
        super(innerException);
    }

    public AppException(Exception innerException, String message) {
        super(message, innerException);
    }
}
