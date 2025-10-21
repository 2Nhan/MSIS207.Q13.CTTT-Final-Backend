package com.crm.project.validator.custom_validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BlankUpdateValidator implements ConstraintValidator<BlankUpdateConstraint, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null) {
            return true;
        }
        return !value.trim().isEmpty();
    }
}
