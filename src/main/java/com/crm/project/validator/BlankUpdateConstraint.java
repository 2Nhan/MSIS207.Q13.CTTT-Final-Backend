package com.crm.project.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {BlankUpdateValidator.class})
public @interface BlankUpdateConstraint {
    String message() default "Update field can not be blank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
