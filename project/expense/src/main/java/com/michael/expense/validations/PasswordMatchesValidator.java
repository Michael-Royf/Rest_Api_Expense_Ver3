package com.michael.expense.validations;

import com.michael.expense.payload.request.ChangePasswordRequest;
import com.michael.expense.payload.request.UserRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof UserRequest) {
            UserRequest userRequest = (UserRequest) obj;
            return userRequest.getPassword().equals(userRequest.getConfirmationPassword());
        } else if (obj instanceof ChangePasswordRequest) {
            ChangePasswordRequest passwordChangeRequest = (ChangePasswordRequest) obj;
            return passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmationPassword());
        } else
            return false;
    }
}
