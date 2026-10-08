package com.accessguard.accessguard.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accessguard.accessguard.entity.AccessGrantEntity;

public interface AccessGrantRepository extends JpaRepository<AccessGrantEntity, UUID> {
}
