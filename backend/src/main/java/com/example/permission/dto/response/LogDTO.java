package com.example.permission.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogDTO {

    private String id;
    private String type;
    private String operation;
    private String target;
    private String userId;
    private String username;
    private String ip;
    private LocalDateTime createdAt;
}