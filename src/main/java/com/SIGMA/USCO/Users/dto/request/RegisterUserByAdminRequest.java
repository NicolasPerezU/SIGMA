package com.SIGMA.USCO.Users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserByAdminRequest {

    private String name;
    private String lastName;
    private String email;
    private String password;
    private String roleName; // PROGRAM_HEAD, PROJECT_DIRECTOR, PROGRAM_CURRICULUM_COMMITTEE
    private Long academicProgramId; // Requerido para roles vinculados a programas

}

