package com.accessguard.accessguard.repository;

import com.accessguard.accessguard.entity.HighRiskActivityFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface HighRiskActivityFlagRepository extends JpaRepository<HighRiskActivityFlag, UUID> {
}