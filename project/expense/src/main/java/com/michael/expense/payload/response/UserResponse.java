package com.michael.expense.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private LocalDateTime registration_timestamp;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private LocalDateTime update_profile_timestamp;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private LocalDateTime lastLoginDate;
}
