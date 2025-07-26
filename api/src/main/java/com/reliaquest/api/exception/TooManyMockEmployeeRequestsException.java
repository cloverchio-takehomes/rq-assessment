package com.reliaquest.api.exception;

public class TooManyMockEmployeeRequestsException extends MockEmployeeServiceException {

    public TooManyMockEmployeeRequestsException(int statusCode) {
        super("An upstream service is handling too many requests", statusCode);
    }
}
