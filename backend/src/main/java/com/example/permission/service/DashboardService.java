package com.example.permission.service;

import com.example.permission.dto.response.StatsDTO;
import com.example.permission.entity.User;
import com.example.permission.repository.PermissionRepository;
import com.example.permission.repository.RoleRepository;
import com.example.permission.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public DashboardService(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Cacheable(value = "dashboardStats", key = "'stats'")
    public StatsDTO getStats() {
        return StatsDTO.builder()
                .totalUsers(userRepository.count())
                .totalRoles(roleRepository.count())
                .totalPermissions(permissionRepository.count())
                .activeUsers(userRepository.countByStatus(User.UserStatus.ACTIVE))
                .build();
    }
}