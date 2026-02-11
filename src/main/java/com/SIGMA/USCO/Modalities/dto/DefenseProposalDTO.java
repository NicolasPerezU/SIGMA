package com.SIGMA.USCO.Modalities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefenseProposalDTO {

    private Long studentModalityId;
    private String studentName;
    private String studentEmail;
    private String studentCode;
    private String modalityName;
    private String academicProgram;
    private String faculty;


    private Long projectDirectorId;
    private String projectDirectorName;
    private String projectDirectorEmail;


    private LocalDateTime proposedDefenseDate;
    private String proposedDefenseLocation;


    private LocalDateTime proposalSubmittedAt;
    private String currentStatus;
    private String statusDescription;
}

