package com.accessguard.accessguard.repository;

import com.accessguard.accessguard.entity.DataAcknowledgment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DataAcknowledgmentRepository extends JpaRepository<DataAcknowledgment, UUID> {
}