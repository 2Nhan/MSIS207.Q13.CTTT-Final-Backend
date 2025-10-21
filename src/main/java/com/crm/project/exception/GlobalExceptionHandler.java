package com.crm.project.exception;

import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.ValidationErrorResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.Comparator;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";
    private static final String MAX_ATTRIBUTE = "max";

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
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<ValidationErrorResponse> errors = new java.util.ArrayList<>(exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    ValidationError validationError = parseEnum(fieldError.getDefaultMessage());

                    var violationConstraint = fieldError.unwrap(ConstraintViolation.class);

                    Map attributes = violationConstraint.getConstraintDescriptor().getAttributes();

                    String message = Objects.nonNull(attributes)
                            ? mapAttribute(validationError.getMessage(), attributes)
                            : validationError.getMessage();

                    return ValidationErrorResponse.builder()
                            .code(validationError.getCode())
                            .fieldError(fieldError.getField())
                            .message(message)
                            .build();
                })
                .toList());
        errors.sort(Comparator.comparing(ValidationErrorResponse::getCode));


        ApiResponse response = ApiResponse.builder()
                .code(2000)
                .message("Validation Failed")
                .result(errors)
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


    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

        String maxValue = String.valueOf(attributes.get(MAX_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue)
                .replace("{" + MAX_ATTRIBUTE + "}", maxValue);
    }
}
