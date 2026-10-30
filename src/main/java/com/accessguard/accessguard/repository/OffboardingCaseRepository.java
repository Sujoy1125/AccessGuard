package com.accessguard.accessguard.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.accessguard.accessguard.entity.OffboardingCase;

public interface OffboardingCaseRepository extends JpaRepository<OffboardingCase, UUID> {
}
