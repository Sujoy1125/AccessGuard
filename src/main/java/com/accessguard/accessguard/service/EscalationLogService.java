package com.accessguard.accessguard.service;

import com.accessguard.accessguard.entity.EscalationLog;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.EscalationLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EscalationLogService {

    private final EscalationLogRepository repository;

    public EscalationLogService(EscalationLogRepository repository) {
        this.repository = repository;
    }

    public List<EscalationLog> findAll() {
        return repository.findAll();
    }

    public EscalationLog findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EscalationLog", id));
    }

    public EscalationLog create(EscalationLog payload) {
        payload.setId(null);
        return repository.save(payload);
    }

    public EscalationLog replace(UUID id, EscalationLog payload) {
        EscalationLog existing = findById(id);
        existing.setRevocationTaskId(payload.getRevocationTaskId());
        existing.setEscalatedTo(payload.getEscalatedTo());
        existing.setEscalatedAt(payload.getEscalatedAt());
        existing.setReason(payload.getReason());
        return repository.save(existing);
    }

    public EscalationLog partialUpdate(UUID id, EscalationLog payload) {
        EscalationLog existing = findById(id);
        if (payload.getRevocationTaskId() != null) {
            existing.setRevocationTaskId(payload.getRevocationTaskId());
        }
        if (payload.getEscalatedTo() != null) {
            existing.setEscalatedTo(payload.getEscalatedTo());
        }
        if (payload.getEscalatedAt() != null) {
            existing.setEscalatedAt(payload.getEscalatedAt());
        }
        if (payload.getReason() != null) {
            existing.setReason(payload.getReason());
        }
        return repository.save(existing);
    }

    // Note: in the finished system, EscalationLog should probably be
    // append-only (matches the "audit trail integrity" claim in the doc).
    // Delete is included here for CRUD completeness / testing today;
    // consider removing this endpoint before the real demo.
    public void delete(UUID id) {
        EscalationLog existing = findById(id);
        repository.delete(existing);
    }
}