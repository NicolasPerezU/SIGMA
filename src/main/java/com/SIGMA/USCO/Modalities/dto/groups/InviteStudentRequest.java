package com.SIGMA.USCO.Modalities.dto.groups;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteStudentRequest {

    private Long studentModalityId;
    private Long inviteeId;
}

