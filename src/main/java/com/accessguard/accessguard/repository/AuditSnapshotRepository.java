package com.accessguard.accessguard.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accessguard.accessguard.entity.AuditSnapshot;

public interface AuditSnapshotRepository extends JpaRepository<AuditSnapshot, UUID> {
}
