CREATE TABLE IF NOT EXISTS `user` (
    `id` VARCHAR(36) PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `status` ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS `idx_user_status` ON `user`(`status`);

CREATE TABLE IF NOT EXISTS `role` (
    `id` VARCHAR(36) PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `description` VARCHAR(255),
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `permission` (
    `id` VARCHAR(36) PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL,
    `code` VARCHAR(100) NOT NULL UNIQUE,
    `description` VARCHAR(255),
    `parent_id` VARCHAR(36),
    `sort_order` INT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_permission_parent` FOREIGN KEY (`parent_id`) REFERENCES `permission`(`id`)
);

CREATE INDEX IF NOT EXISTS `idx_permission_parent` ON `permission`(`parent_id`);

CREATE TABLE IF NOT EXISTS `user_role` (
    `user_id` VARCHAR(36) NOT NULL,
    `role_id` VARCHAR(36) NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `role_permission` (
    `role_id` VARCHAR(36) NOT NULL,
    `permission_code` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`role_id`, `permission_code`),
    CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `role`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_code`) REFERENCES `permission`(`code`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `system_log` (
    `id` VARCHAR(36) PRIMARY KEY,
    `type` ENUM('LOGIN', 'OPERATION', 'ERROR') NOT NULL,
    `operation` VARCHAR(100) NOT NULL,
    `target` VARCHAR(255),
    `user_id` VARCHAR(36),
    `ip` VARCHAR(50),
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_log_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

CREATE INDEX IF NOT EXISTS `idx_log_type` ON `system_log`(`type`);
CREATE INDEX IF NOT EXISTS `idx_log_user` ON `system_log`(`user_id`);
CREATE INDEX IF NOT EXISTS `idx_log_created` ON `system_log`(`created_at`);