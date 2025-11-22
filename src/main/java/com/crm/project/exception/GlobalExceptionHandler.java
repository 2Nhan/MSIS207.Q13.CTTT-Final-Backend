package com.crm.project.exception;

import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.AppErrorResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingException(Exception exception) {
        log.error("Exception: ", exception);
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        AppErrorResponse appErrorResponse = AppErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errorField(exception.getErrorField())
                .build();
        ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getStatusCode().value())
                .message("Process Failed")
                .error(appErrorResponse)
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse> handlingMissingServletRequestPartException(
            MissingServletRequestPartException exception) {

        String part = exception.getRequestPartName();
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_PART;

        Map<String, Object> attributes = Map.of(
                "part", part
        );

        // build dynamic message
        String finalMessage = mapAttributes(errorCode.getMessage(), attributes);

        AppErrorResponse appErrorResponse = AppErrorResponse.builder()
                .code(errorCode.getCode())
                .message(finalMessage)
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getStatusCode().value())
                .message("Missing request part")
                .error(appErrorResponse)
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse> handlingHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        ErrorCode errorCode = ErrorCode.INVALID_FILE_EXTENSION;
        AppErrorResponse appErrorResponse = AppErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        ApiResponse apiResponse = ApiResponse.builder()
                .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .message("Process Failed")
                .error(appErrorResponse)
                .build();
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<AppErrorResponse> errors = new ArrayList<>(exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    ValidationError validationError = parseEnum(fieldError.getDefaultMessage());

                    var violationConstraint = fieldError.unwrap(ConstraintViolation.class);

                    Map attributes = violationConstraint.getConstraintDescriptor().getAttributes();

                    String message = Objects.nonNull(attributes)
                            ? mapAttributes(validationError.getMessage(), attributes)
                            : validationError.getMessage();

                    return AppErrorResponse.builder()
                            .code(validationError.getCode())
                            .errorField(fieldError.getField())
                            .message(message)
                            .build();
                })
                .toList());
        errors.sort(Comparator.comparing(AppErrorResponse::getCode));


        ApiResponse response = ApiResponse.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message("Validation Failed")
                .error(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    private ValidationError parseEnum(String key) {
        try {
            return ValidationError.valueOf(key);
        } catch (IllegalArgumentException e) {
            return ValidationError.INVALID_KEY;
        }
    }


    private String mapAttributes(String template, Map<String, Object> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return template;
        }

        String result = template;
        for (var entry : attributes.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            result = result.replace(placeholder, String.valueOf(entry.getValue()));
        }
        return result;
    }
}
