package com.michael.expense.payload.response;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private LocalDateTime registration_timestamp;
    private LocalDateTime update_profile_timestamp;
}
