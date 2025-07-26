package com.reliaquest.api.exception;

public class InvalidEmployeeException extends MockEmployeeServiceException {

    public InvalidEmployeeException(String employeeContent, int statusCode) {
        super(String.format("Invalid employee data: %s", employeeContent), statusCode);
    }
}
