package com.crm.project.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_EXSITED(1001, "Username exsited, please fill in another username", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_FOUND(1002, "Username not found", HttpStatus.NOT_FOUND),
    EMAIL_EXSITED(1003, "Email exsited, this email has been register", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXSITED(1004, "Phone number exsited, this phone has been register", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    NO_RESULTS(1006, "No results", HttpStatus.NOT_FOUND),

    INVALID_FILE_EXTENSION(1100, "File extension must be .jpg or .png", HttpStatus.BAD_REQUEST),
    INVALID_FILE_SIZE(1101, "File size limit is 1MB", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
