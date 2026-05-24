package com.example.permission.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "system_log", indexes = {
    @Index(name = "idx_log_type", columnList = "type"),
    @Index(name = "idx_log_user", columnList = "user_id"),
    @Index(name = "idx_log_created", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
public class SystemLog {

    @Id
    @Column(length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LogType type;

    @Column(nullable = false, length = 100)
    private String operation;

    @Column(length = 255)
    private String target;

    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(length = 50)
    private String ip;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum LogType {
        LOGIN, OPERATION, ERROR
    }
}