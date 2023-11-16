package com.michael.expense.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginRequest {
    @NotBlank(message = "Username or Email should not be empty")
    private String usernameOrEmail;
    @NotBlank(message = "Password should not be empty")
    private String password;
}
