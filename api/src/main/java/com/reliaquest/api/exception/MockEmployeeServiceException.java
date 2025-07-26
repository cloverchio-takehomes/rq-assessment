package com.reliaquest.api.exception;

import lombok.Getter;

@Getter
public class MockEmployeeServiceException extends RuntimeException {

    private int statusCode;

    public MockEmployeeServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public MockEmployeeServiceException(int statusCode) {
        super(String.format("An upstream service failed with status: %d", statusCode));
    }
}
