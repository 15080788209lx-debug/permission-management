package com.example.permission.service;

import com.example.permission.config.JwtConfig;
import com.example.permission.dto.request.LoginRequest;
import com.example.permission.dto.response.LoginResponse;
import com.example.permission.entity.User;
import com.example.permission.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final Map<String, String> refreshTokenStore = new HashMap<>();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtConfig jwtConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtConfig = jwtConfig;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() == User.UserStatus.INACTIVE) {
            throw new RuntimeException("用户已被禁用");
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        String accessToken = jwtConfig.generateAccessToken(user.getId(), user.getUsername(), roles);
        String refreshToken = jwtConfig.generateRefreshToken(user.getId());

        refreshTokenStore.put(user.getId(), refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtConfig.getAccessTokenExpire())
                .user(LoginResponse.UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .roles(roles)
                        .build())
                .build();
    }

    public void logout(String userId) {
        refreshTokenStore.remove(userId);
    }

    public LoginResponse refreshToken(String refreshToken) {
        try {
            String userId = jwtConfig.extractUserId(refreshToken);
            
            if (!refreshToken.equals(refreshTokenStore.get(userId))) {
                throw new RuntimeException("无效的刷新令牌");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            List<String> roles = user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList());

            String newAccessToken = jwtConfig.generateAccessToken(user.getId(), user.getUsername(), roles);
            String newRefreshToken = jwtConfig.generateRefreshToken(user.getId());

            refreshTokenStore.put(userId, newRefreshToken);

            return LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .expiresIn(jwtConfig.getAccessTokenExpire())
                    .user(LoginResponse.UserDTO.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .roles(roles)
                            .build())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("刷新令牌无效");
        }
    }
}