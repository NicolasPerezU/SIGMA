package com.SIGMA.USCO.Users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignRoleRequest {

    private Long userId;
    private Long roleId;

}
