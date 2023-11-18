package com.michael.expense.payload.request;

import com.michael.expense.validations.PasswordMatches;
import com.michael.expense.validations.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@PasswordMatches
public class ChangePasswordRequest {
    @NotBlank(message = "Old password should not be empty")
    private String currentPassword;
    @NotBlank(message = "New password should not be empty")
    @ValidPassword
    private String newPassword;
    @NotBlank(message = "Confirmation password should not be empty")
    private String confirmationPassword;
}
