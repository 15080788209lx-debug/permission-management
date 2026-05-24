package com.example.permission.controller;

import com.example.permission.dto.request.RoleCreateRequest;
import com.example.permission.dto.response.ApiResponse;
import com.example.permission.dto.response.RoleDTO;
import com.example.permission.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ApiResponse<Page<RoleDTO>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RoleDTO> roles = roleService.findAll(keyword, pageable);
        return ApiResponse.success(roles);
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleDTO> getRoleById(@PathVariable String id) {
        RoleDTO role = roleService.findById(id);
        return ApiResponse.success(role);
    }

    @PostMapping
    public ApiResponse<RoleDTO> createRole(@Valid @RequestBody RoleCreateRequest request) {
        RoleDTO role = roleService.create(request);
        return ApiResponse.success("角色创建成功", role);
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleDTO> updateRole(@PathVariable String id, @RequestBody RoleCreateRequest request) {
        RoleDTO role = roleService.update(id, request);
        return ApiResponse.success("角色更新成功", role);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable String id) {
        roleService.delete(id);
        return ApiResponse.success("角色删除成功", null);
    }
}