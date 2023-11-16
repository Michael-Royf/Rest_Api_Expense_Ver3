package com.michael.expense.payload.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRequest {
    //TODO validation
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
