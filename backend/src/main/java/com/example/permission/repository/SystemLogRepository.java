package com.example.permission.repository;

import com.example.permission.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, String> {

    Page<SystemLog> findByType(SystemLog.LogType type, Pageable pageable);

    Page<SystemLog> findByUserId(String userId, Pageable pageable);
}