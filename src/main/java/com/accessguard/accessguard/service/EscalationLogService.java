package com.accessguard.accessguard.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.accessguard.accessguard.entity.EscalationLog;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.EscalationLogRepository;

@Service
public class EscalationLogService {

    private static final Logger log = LoggerFactory.getLogger(EscalationLogService.class);

    private final EscalationLogRepository repository;

    public EscalationLogService(EscalationLogRepository repository) {
        this.repository = repository;
    }

    public List<EscalationLog> findAll() {
        log.debug("Fetching all EscalationLog records");
        return repository.findAll();
    }

    public EscalationLog findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("EscalationLog not found id={}", id);
                    return new ResourceNotFoundException("EscalationLog", id);
                });
    }

    public EscalationLog create(EscalationLog payload) {
        payload.setId(null);
        EscalationLog saved = repository.save(payload);
        log.info("Created EscalationLog id={} revocationTaskId={} escalatedTo={}",
                saved.getId(), saved.getRevocationTaskId(), saved.getEscalatedTo());
        return saved;
    }

    public EscalationLog replace(UUID id, EscalationLog payload) {
        EscalationLog existing = findById(id);
        existing.setRevocationTaskId(payload.getRevocationTaskId());
        existing.setEscalatedTo(payload.getEscalatedTo());
        existing.setEscalatedAt(payload.getEscalatedAt());
        existing.setReason(payload.getReason());
        EscalationLog saved = repository.save(existing);
        log.info("Replaced EscalationLog id={}", saved.getId());
        return saved;
    }

    public EscalationLog partialUpdate(UUID id, EscalationLog payload) {
        EscalationLog existing = findById(id);
        if (payload.getRevocationTaskId() != null)
            existing.setRevocationTaskId(payload.getRevocationTaskId());
        if (payload.getEscalatedTo() != null)
            existing.setEscalatedTo(payload.getEscalatedTo());
        if (payload.getEscalatedAt() != null)
            existing.setEscalatedAt(payload.getEscalatedAt());
        if (payload.getReason() != null)
            existing.setReason(payload.getReason());
        EscalationLog saved = repository.save(existing);
        log.info("Patched EscalationLog id={}", saved.getId());
        return saved;
    }

    public void delete(UUID id) {
        EscalationLog existing = findById(id);
        repository.delete(existing);
        log.warn("Deleted EscalationLog id={} (append-only trail — verify this was intentional)", id);
    }
}