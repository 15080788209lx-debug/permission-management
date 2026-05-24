package com.example.permission.service;

import com.example.permission.dto.response.LogDTO;
import com.example.permission.entity.SystemLog;
import com.example.permission.entity.User;
import com.example.permission.repository.SystemLogRepository;
import com.example.permission.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LogService {

    private final SystemLogRepository logRepository;
    private final UserRepository userRepository;

    public LogService(SystemLogRepository logRepository, UserRepository userRepository) {
        this.logRepository = logRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createLog(SystemLog.LogType type, String operation, String target, String userId, String ip) {
        SystemLog log = SystemLog.builder()
                .id(UUID.randomUUID().toString())
                .type(type)
                .operation(operation)
                .target(target)
                .userId(userId)
                .ip(ip)
                .build();
        
        logRepository.save(log);
    }

    public Page<LogDTO> findAll(Pageable pageable) {
        Page<SystemLog> logPage = logRepository.findAll(pageable);
        
        return logPage.map(log -> {
            User user = null;
            if (log.getUserId() != null) {
                user = userRepository.findById(log.getUserId()).orElse(null);
            }
            
            return LogDTO.builder()
                    .id(log.getId())
                    .type(log.getType().name())
                    .operation(log.getOperation())
                    .target(log.getTarget())
                    .userId(log.getUserId())
                    .username(user != null ? user.getUsername() : null)
                    .ip(log.getIp())
                    .createdAt(log.getCreatedAt())
                    .build();
        });
    }
}