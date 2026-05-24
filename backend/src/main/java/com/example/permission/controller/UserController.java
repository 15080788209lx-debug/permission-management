package com.example.permission.controller;

import com.example.permission.dto.request.UserCreateRequest;
import com.example.permission.dto.request.UserUpdateRequest;
import com.example.permission.dto.response.ApiResponse;
import com.example.permission.dto.response.UserDTO;
import com.example.permission.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserDTO> users = userService.findAll(keyword, pageable);
        return ApiResponse.success(users);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(@PathVariable String id) {
        UserDTO user = userService.findById(id);
        return ApiResponse.success(user);
    }

    @PostMapping
    public ApiResponse<UserDTO> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserDTO user = userService.create(request);
        return ApiResponse.success("用户创建成功", user);
    }

    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request) {
        UserDTO user = userService.update(id, request);
        return ApiResponse.success("用户更新成功", user);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable String id) {
        userService.delete(id);
        return ApiResponse.success("用户删除成功", null);
    }
}