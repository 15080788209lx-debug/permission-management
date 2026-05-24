package com.example.permission.repository;

import com.example.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

    Optional<Permission> findByCode(String code);

    boolean existsByCode(String code);

    List<Permission> findByParentId(String parentId);

    List<Permission> findByParentIdIsNullOrderBySortOrderAsc();
}