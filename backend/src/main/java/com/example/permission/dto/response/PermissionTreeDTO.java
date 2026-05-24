package com.example.permission.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionTreeDTO {

    private String id;
    private String name;
    private String code;
    private String description;
    private List<PermissionTreeDTO> children;
    private LocalDateTime createdAt;
}