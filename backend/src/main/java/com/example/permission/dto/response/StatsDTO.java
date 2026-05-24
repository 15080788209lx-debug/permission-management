package com.example.permission.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {

    private Long totalUsers;
    private Long totalRoles;
    private Long totalPermissions;
    private Long activeUsers;
}