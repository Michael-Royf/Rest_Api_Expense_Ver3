package com.michael.expense.payload.request;

import com.michael.expense.validations.PasswordMatches;
import com.michael.expense.validations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@PasswordMatches
public class UserRequest {
    @NotBlank(message = "Username should not be empty")
    @Pattern(regexp = "^[^\\s]+$", message = "Username should not contain a space")
    private String username;
    @NotBlank(message = "First name should not be empty")
    @Pattern(regexp = "^(?!\\s)(.*\\S)$", message = "The firstName should not start or end with a space")
    private String firstName;
    @NotBlank(message = "Last name should not be empty")
    @Pattern(regexp = "^(?!\\s)(.*\\S)$", message = "The lastName should not start or end with a space")
    private String lastName;
    @Email
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "не соответствует паттерну")
    @NotBlank(message = "Email should not be empty")
    private String email;

    @NotBlank(message = "Password should not be empty")
    @ValidPassword
    private String password;
    @NotBlank(message = "Confirmation password should not be empty")
    private String confirmationPassword;
}
