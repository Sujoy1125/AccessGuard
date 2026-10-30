package com.accessguard.accessguard.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accessguard.accessguard.entity.DataAcknowledgment;

public interface DataAcknowledgmentRepository extends JpaRepository<DataAcknowledgment, UUID> {
}
