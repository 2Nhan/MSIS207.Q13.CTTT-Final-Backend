package com.crm.project.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_EXSITED(1001, "Username exsited, please fill in another username", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    EMAIL_EXSITED(1003, "Email exsited, this email has been register", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXSITED(1004, "Phone number exsited, this phone has been register", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    NO_RESULTS(1006, "No results", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(1007, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_SKU_EXISTED(1008, "Product sku already exists", HttpStatus.BAD_REQUEST),
    WRONG_MATCHING(1009, "Wrong matching, matching request is not suitable to the given file", HttpStatus.BAD_REQUEST),
    STAGE_NOT_FOUND(1010, "Stage not found", HttpStatus.NOT_FOUND),
    LEAD_NOT_FOUND(1011, "Lead not found", HttpStatus.NOT_FOUND),

    INVALID_FILE_TYPE(1100, "File type is not supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    INVALID_FILE_SIZE(1101, "File size limit is 1MB", HttpStatus.BAD_REQUEST),
    EMPTY_FILE(1102, "Empty file", HttpStatus.BAD_REQUEST),
    LIMIT_ROWS_EXCEEDED(1103, "Number of rows limit is 100", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST_PART(1104, "Required part '{part}' is not presented", HttpStatus.BAD_REQUEST),
    MISSING_FILE(1105, "Required file does not exist", HttpStatus.BAD_REQUEST),
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
