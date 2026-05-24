package com.example.permission.service;

import com.example.permission.dto.request.RoleCreateRequest;
import com.example.permission.dto.response.RoleDTO;
import com.example.permission.entity.Permission;
import com.example.permission.entity.Role;
import com.example.permission.repository.PermissionRepository;
import com.example.permission.repository.RoleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Cacheable(value = "roles", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + (#keyword ?: '')")
    public Page<RoleDTO> findAll(String keyword, Pageable pageable) {
        Page<Role> rolePage;
        
        if (keyword != null && !keyword.isEmpty()) {
            rolePage = roleRepository.findByKeyword(keyword, pageable);
        } else {
            rolePage = roleRepository.findAll(pageable);
        }

        return rolePage.map(this::convertToDTO);
    }

    @Cacheable(value = "role", key = "#id")
    public RoleDTO findById(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        return convertToDTO(role);
    }

    @Transactional
    @CacheEvict(value = {"roles", "role"}, allEntries = true)
    public RoleDTO create(RoleCreateRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("角色名称已存在");
        }

        Role role = Role.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .permissions(resolvePermissions(request.getPermissionCodes()))
                .build();

        Role savedRole = roleRepository.save(role);
        return convertToDTO(savedRole);
    }

    @Transactional
    @CacheEvict(value = {"roles", "role"}, allEntries = true)
    public RoleDTO update(String id, RoleCreateRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在"));

        role.setDescription(request.getDescription());
        role.setPermissions(resolvePermissions(request.getPermissionCodes()));

        Role updatedRole = roleRepository.save(role);
        return convertToDTO(updatedRole);
    }

    @Transactional
    @CacheEvict(value = {"roles", "role"}, allEntries = true)
    public void delete(String id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("角色不存在");
        }
        roleRepository.deleteById(id);
    }

    private Set<Permission> resolvePermissions(List<String> permissionCodes) {
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return new HashSet<>();
        }
        return permissionCodes.stream()
                .map(code -> permissionRepository.findByCode(code)
                        .orElseThrow(() -> new RuntimeException("权限不存在: " + code)))
                .collect(Collectors.toSet());
    }

    private RoleDTO convertToDTO(Role role) {
        List<RoleDTO.PermissionDTO> permissions = role.getPermissions().stream()
                .map(p -> RoleDTO.PermissionDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .code(p.getCode())
                        .build())
                .collect(Collectors.toList());

        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissions)
                .permissionCount(permissions.size())
                .createdAt(role.getCreatedAt())
                .build();
    }
}