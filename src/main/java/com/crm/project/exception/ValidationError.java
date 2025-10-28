package com.crm.project.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ValidationError {
    INVALID_KEY(9999, "Invalid key", HttpStatus.INTERNAL_SERVER_ERROR),

    BLANK_USERNAME(2001, "PLease fill in username", HttpStatus.BAD_REQUEST),
    BLANK_PASSWORD(2002, "PLease fill in password", HttpStatus.BAD_REQUEST),
    BLANK_FIRSTNAME(2003, "PLease fill in first name", HttpStatus.BAD_REQUEST),
    BLANK_LASTNAME(2004, "PLease fill in last name", HttpStatus.BAD_REQUEST),
    BLANK_EMAIL(2005, "PLease fill in email", HttpStatus.BAD_REQUEST),
    BLANK_PHONE_NUMBER(2006, "PLease fill in phone number", HttpStatus.BAD_REQUEST),
    BLANK_UPDATE_FIELD(2007, "This field can not be blank", HttpStatus.BAD_REQUEST),
    BLANK_PRODUCT_NAME(2008, "PLease fill in product name", HttpStatus.BAD_REQUEST),

    INVALID_USERNAME(3001, "Username must have {min}-{max} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(3002, "Password must have at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(3003, "Please fill in valid email", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER(3004, "Please fill in valid phone number", HttpStatus.BAD_REQUEST),
    ;

    ValidationError(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
