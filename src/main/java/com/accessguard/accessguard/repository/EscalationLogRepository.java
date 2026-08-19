package com.accessguard.accessguard.repository;

import com.accessguard.accessguard.entity.EscalationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EscalationLogRepository extends JpaRepository<EscalationLog, UUID> {
}