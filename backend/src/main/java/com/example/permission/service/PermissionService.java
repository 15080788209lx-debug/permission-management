package com.example.permission.service;

import com.example.permission.dto.response.PermissionTreeDTO;
import com.example.permission.entity.Permission;
import com.example.permission.repository.PermissionRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Cacheable(value = "permissionTree", key = "'tree'")
    public List<PermissionTreeDTO> getPermissionTree() {
        List<Permission> allPermissions = permissionRepository.findAll();
        
        Map<String, List<Permission>> groupedByParent = allPermissions.stream()
                .collect(Collectors.groupingBy(p -> p.getParentId() != null ? p.getParentId() : "ROOT"));

        List<Permission> rootPermissions = groupedByParent.getOrDefault("ROOT", new ArrayList<>());
        
        return rootPermissions.stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(permission -> buildTree(permission, groupedByParent))
                .collect(Collectors.toList());
    }

    private PermissionTreeDTO buildTree(Permission permission, Map<String, List<Permission>> groupedByParent) {
        List<Permission> children = groupedByParent.getOrDefault(permission.getId(), new ArrayList<>());
        
        List<PermissionTreeDTO> childDTOs = children.stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(child -> buildTree(child, groupedByParent))
                .collect(Collectors.toList());

        return PermissionTreeDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .code(permission.getCode())
                .description(permission.getDescription())
                .children(childDTOs.isEmpty() ? null : childDTOs)
                .createdAt(permission.getCreatedAt())
                .build();
    }

    public long count() {
        return permissionRepository.count();
    }
}