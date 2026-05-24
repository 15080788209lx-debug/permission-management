# 权限管理系统 - 部署文档

## 1. 环境要求

### 1.1 前端环境
- Node.js >= 18.0.0
- npm >= 9.0.0

### 1.2 后端环境
- Java JDK 17
- Maven >= 3.8.0

### 1.3 中间件
- MySQL 8.0+
- Redis 8.0+
- RabbitMQ 3.10+
- Elasticsearch 7.17.3

## 2. 数据库配置

### 2.1 创建数据库

```sql
CREATE DATABASE example_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON example_db.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;
```

### 2.2 初始化数据

数据库表结构会通过 JPA 自动创建。首次启动时，系统会自动初始化管理员账户：
- 用户名: `admin`
- 密码: `admin123`

## 3. 后端部署

### 3.1 配置文件

修改 `backend/src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/example_db?useSSL=false&serverTimezone=Asia/Shanghai
    username: admin
    password: password
  data:
    redis:
      host: localhost
      port: 6379
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

jwt:
  secret: your-256-bit-secret-key-here-must-be-at-least-32-characters-long-for-hs256
  access-token-expire: 86400000
  refresh-token-expire: 604800000
```

### 3.2 启动方式

**开发环境:**
```bash
cd backend
mvn spring-boot:run
```

**生产环境打包:**
```bash
cd backend
mvn clean package
java -jar target/permission-management-1.0.0.jar
```

## 4. 前端部署

### 4.1 安装依赖

```bash
cd frontend
npm install
```

### 4.2 开发模式

```bash
npm run dev
```

访问地址: http://localhost:5173

### 4.3 生产构建

```bash
npm run build
```

构建产物位于 `dist` 目录。

## 5. Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /path/to/frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 6. 服务启动顺序

1. MySQL (端口: 3306)
2. Redis (端口: 6379)
3. RabbitMQ (端口: 5672)
4. Elasticsearch (端口: 9200)
5. 后端服务 (端口: 8080)
6. 前端服务 (端口: 5173)

## 7. 默认账户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |

## 8. API 接口

### 8.1 认证接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/logout | 用户登出 |
| POST | /api/auth/refresh | 刷新 Token |
| GET | /api/auth/me | 获取当前用户 |

### 8.2 用户管理

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/users | 获取用户列表 |
| POST | /api/users | 创建用户 |
| GET | /api/users/{id} | 获取用户详情 |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |

### 8.3 角色管理

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/roles | 获取角色列表 |
| POST | /api/roles | 创建角色 |
| GET | /api/roles/{id} | 获取角色详情 |
| PUT | /api/roles/{id} | 更新角色 |
| DELETE | /api/roles/{id} | 删除角色 |

### 8.4 权限管理

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/permissions/tree | 获取权限树 |

### 8.5 仪表盘

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/dashboard/stats | 获取统计数据 |

### 8.6 日志管理

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/logs | 获取系统日志 |