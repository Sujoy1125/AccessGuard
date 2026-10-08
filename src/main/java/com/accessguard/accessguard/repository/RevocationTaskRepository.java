package com.accessguard.accessguard.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accessguard.accessguard.entity.RevocationTask;

public interface RevocationTaskRepository extends JpaRepository<RevocationTask, UUID> {
}
