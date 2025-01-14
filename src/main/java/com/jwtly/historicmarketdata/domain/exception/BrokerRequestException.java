package com.jwtly.historicmarketdata.domain.exception;

public class BrokerRequestException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    public BrokerRequestException(String message, int statusCode, String responseBody, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}