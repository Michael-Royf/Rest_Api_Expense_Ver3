package com.michael.expense.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.michael.expense.entity.Expense;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Israel")
    private LocalDateTime lastLoginDate;
}
