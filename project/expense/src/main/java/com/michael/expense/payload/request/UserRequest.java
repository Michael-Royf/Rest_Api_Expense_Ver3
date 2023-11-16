package com.michael.expense.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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
    @NotBlank(message = "Email should not be empty")
    private String email;
    //todo: matching password
    @NotBlank(message = "Password should not be empty")
    private String password;
}
