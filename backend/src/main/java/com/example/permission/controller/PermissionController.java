package com.example.permission.controller;

import com.example.permission.dto.response.ApiResponse;
import com.example.permission.dto.response.PermissionTreeDTO;
import com.example.permission.service.PermissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/tree")
    public ApiResponse<List<PermissionTreeDTO>> getPermissionTree() {
        List<PermissionTreeDTO> tree = permissionService.getPermissionTree();
        return ApiResponse.success(tree);
    }
}