package com.example.permission.config;

import com.example.permission.entity.Permission;
import com.example.permission.entity.Role;
import com.example.permission.entity.User;
import com.example.permission.repository.PermissionRepository;
import com.example.permission.repository.RoleRepository;
import com.example.permission.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, 
                          PermissionRepository permissionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initPermissions();
        initRoles();
        initUsers();
    }

    private void initPermissions() {
        if (permissionRepository.count() > 0) {
            updatePermissionNames();
            return;
        }

        String[] modules = {"user", "role", "permission", "log"};
        String[] actions = {"list", "create", "edit", "delete"};
        String[] moduleNames = {"用户", "角色", "权限", "日志"};
        String[] actionNames = {"列表", "创建", "编辑", "删除"};

        for (int i = 0; i < modules.length; i++) {
            String module = modules[i];
            String moduleName = moduleNames[i];
            
            Permission parent = Permission.builder()
                    .id(UUID.randomUUID().toString())
                    .name(moduleName + "管理")
                    .code(module + ":manage")
                    .description(moduleName + "模块管理权限")
                    .parentId(null)
                    .sortOrder(0)
                    .build();
            permissionRepository.save(parent);

            for (int j = 0; j < actions.length; j++) {
                String action = actions[j];
                String actionName = actionNames[j];
                
                Permission child = Permission.builder()
                        .id(UUID.randomUUID().toString())
                        .name(moduleName + actionName)
                        .code(module + ":" + action)
                        .description(moduleName + actionName + "权限")
                        .parentId(parent.getId())
                        .sortOrder(j + 1)
                        .build();
                permissionRepository.save(child);
            }
        }
    }

    private void updatePermissionNames() {
        String[] modules = {"user", "role", "permission", "log"};
        String[] moduleNames = {"用户", "角色", "权限", "日志"};
        String[] actions = {"list", "create", "edit", "delete"};
        String[] actionNames = {"列表", "创建", "编辑", "删除"};

        for (int i = 0; i < modules.length; i++) {
            String module = modules[i];
            String moduleName = moduleNames[i];
            
            permissionRepository.findByCode(module + ":manage").ifPresent(perm -> {
                perm.setName(moduleName + "管理");
                perm.setDescription(moduleName + "模块管理权限");
                permissionRepository.save(perm);
            });

            for (int j = 0; j < actions.length; j++) {
                String action = actions[j];
                String actionName = actionNames[j];
                
                permissionRepository.findByCode(module + ":" + action).ifPresent(perm -> {
                    perm.setName(moduleName + actionName);
                    perm.setDescription(moduleName + actionName + "权限");
                    permissionRepository.save(perm);
                });
            }
        }
    }

    private void initRoles() {

        Permission userManage = permissionRepository.findByCode("user:manage").orElse(null);
        Permission userList = permissionRepository.findByCode("user:list").orElse(null);
        Permission userCreate = permissionRepository.findByCode("user:create").orElse(null);
        Permission userEdit = permissionRepository.findByCode("user:edit").orElse(null);
        Permission userDelete = permissionRepository.findByCode("user:delete").orElse(null);
        
        Permission roleManage = permissionRepository.findByCode("role:manage").orElse(null);
        Permission roleList = permissionRepository.findByCode("role:list").orElse(null);
        Permission roleCreate = permissionRepository.findByCode("role:create").orElse(null);
        Permission roleEdit = permissionRepository.findByCode("role:edit").orElse(null);
        Permission roleDelete = permissionRepository.findByCode("role:delete").orElse(null);
        
        Permission permManage = permissionRepository.findByCode("permission:manage").orElse(null);
        Permission permList = permissionRepository.findByCode("permission:list").orElse(null);
        
        Permission logManage = permissionRepository.findByCode("log:manage").orElse(null);
        Permission logList = permissionRepository.findByCode("log:list").orElse(null);

        Set<Permission> adminPermissions = new HashSet<>();
        if (userManage != null) adminPermissions.add(userManage);
        if (roleManage != null) adminPermissions.add(roleManage);
        if (permManage != null) adminPermissions.add(permManage);
        if (logManage != null) adminPermissions.add(logManage);

        if (!roleRepository.existsByName("ADMIN")) {
            Role adminRole = Role.builder()
                    .id(UUID.randomUUID().toString())
                    .name("ADMIN")
                    .description("超级管理员 - 拥有所有权限")
                    .permissions(adminPermissions)
                    .build();
            roleRepository.save(adminRole);
        }

        if (!roleRepository.existsByName("EDITOR")) {
            Set<Permission> editorPermissions = new HashSet<>();
            if (userList != null) editorPermissions.add(userList);
            if (userCreate != null) editorPermissions.add(userCreate);
            if (userEdit != null) editorPermissions.add(userEdit);
            if (roleList != null) editorPermissions.add(roleList);
            if (roleCreate != null) editorPermissions.add(roleCreate);
            if (roleEdit != null) editorPermissions.add(roleEdit);
            if (permList != null) editorPermissions.add(permList);
            if (logList != null) editorPermissions.add(logList);

            Role editorRole = Role.builder()
                    .id(UUID.randomUUID().toString())
                    .name("EDITOR")
                    .description("编辑员 - 可创建和编辑内容")
                    .permissions(editorPermissions)
                    .build();
            roleRepository.save(editorRole);
        }

        if (!roleRepository.existsByName("VIEWER")) {
            Set<Permission> viewerPermissions = new HashSet<>();
            if (userList != null) viewerPermissions.add(userList);
            if (roleList != null) viewerPermissions.add(roleList);
            if (permList != null) viewerPermissions.add(permList);
            if (logList != null) viewerPermissions.add(logList);

            Role viewerRole = Role.builder()
                    .id(UUID.randomUUID().toString())
                    .name("VIEWER")
                    .description("查看员 - 仅可查看数据")
                    .permissions(viewerPermissions)
                    .build();
            roleRepository.save(viewerRole);
        }

        if (!roleRepository.existsByName("USER")) {
            Set<Permission> userPermissions = new HashSet<>();
            if (userList != null) userPermissions.add(userList);
            if (logList != null) userPermissions.add(logList);

            Role userRole = Role.builder()
                    .id(UUID.randomUUID().toString())
                    .name("USER")
                    .description("普通用户 - 基础访问权限")
                    .permissions(userPermissions)
                    .build();
            roleRepository.save(userRole);
        }
    }

    private void initUsers() {
        if (userRepository.existsByUsername("admin")) return;

        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        Set<Role> roles = new HashSet<>();
        if (adminRole != null) roles.add(adminRole);

        User admin = User.builder()
                .id(UUID.randomUUID().toString())
                .username("admin")
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin123"))
                .status(User.UserStatus.ACTIVE)
                .roles(roles)
                .build();
        userRepository.save(admin);
    }
}