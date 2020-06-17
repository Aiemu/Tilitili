package com.example.tilitili.http;

public class ErrorMessage {
    private String errorMessage;
    private int errorCode;
    private String url;
    private String timestamp;

    public ErrorMessage(String errorMessage, int errorCode, String url, String timestamp) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.url = url;
        this.timestamp = timestamp;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getUrl() {
        return url;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
