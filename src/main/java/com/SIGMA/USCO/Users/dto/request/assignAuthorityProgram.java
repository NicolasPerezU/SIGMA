package com.SIGMA.USCO.Users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class assignAuthorityProgram {

    private Long userId;
    private Long academicProgramId;

}
