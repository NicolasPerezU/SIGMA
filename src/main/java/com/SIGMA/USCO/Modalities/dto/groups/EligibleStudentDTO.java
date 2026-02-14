package com.SIGMA.USCO.Modalities.dto.groups;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibleStudentDTO {

    private Long userId;
    private String fullName;
    private String academicProgramName;
    private Long currentSemester;
    private String studentCode;

}

