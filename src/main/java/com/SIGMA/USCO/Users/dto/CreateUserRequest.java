package com.SIGMA.USCO.Users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CreateUserRequest {
    private String name;
    private String lastName;
    private String email;
    private String password;

}
