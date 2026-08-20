package com.accessguard.accessguard.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.accessguard.accessguard.entity.AuditSnapshot;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.AuditSnapshotRepository;

@Service
public class AuditSnapshotService {

    private static final Logger log = LoggerFactory.getLogger(AuditSnapshotService.class);

    private final AuditSnapshotRepository repository;

    public AuditSnapshotService(AuditSnapshotRepository repository) {
        this.repository = repository;
    }

    public List<AuditSnapshot> findAll() {
        log.debug("Fetching all AuditSnapshot records");
        return repository.findAll();
    }

    public AuditSnapshot findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("AuditSnapshot not found id={}", id);
                    return new ResourceNotFoundException("AuditSnapshot", id);
                });
    }

    public AuditSnapshot create(AuditSnapshot payload) {
        payload.setId(null);
        AuditSnapshot saved = repository.save(payload);
        log.info("Created AuditSnapshot id={} period={}..{}", saved.getId(), saved.getPeriodStart(),
                saved.getPeriodEnd());
        return saved;
    }

    public AuditSnapshot replace(UUID id, AuditSnapshot payload) {
        AuditSnapshot existing = findById(id);
        existing.setPeriodStart(payload.getPeriodStart());
        existing.setPeriodEnd(payload.getPeriodEnd());
        existing.setTotalOffboardings(payload.getTotalOffboardings());
        existing.setPctFullyRevokedOnTime(payload.getPctFullyRevokedOnTime());
        existing.setAvgRevocationTimeHrs(payload.getAvgRevocationTimeHrs());
        existing.setOverdueCasesCount(payload.getOverdueCasesCount());
        AuditSnapshot saved = repository.save(existing);
        log.info("Replaced AuditSnapshot id={}", saved.getId());
        return saved;
    }

    public AuditSnapshot partialUpdate(UUID id, AuditSnapshot payload) {
        AuditSnapshot existing = findById(id);
        if (payload.getPeriodStart() != null)
            existing.setPeriodStart(payload.getPeriodStart());
        if (payload.getPeriodEnd() != null)
            existing.setPeriodEnd(payload.getPeriodEnd());
        if (payload.getTotalOffboardings() != null)
            existing.setTotalOffboardings(payload.getTotalOffboardings());
        if (payload.getPctFullyRevokedOnTime() != null)
            existing.setPctFullyRevokedOnTime(payload.getPctFullyRevokedOnTime());
        if (payload.getAvgRevocationTimeHrs() != null)
            existing.setAvgRevocationTimeHrs(payload.getAvgRevocationTimeHrs());
        if (payload.getOverdueCasesCount() != null)
            existing.setOverdueCasesCount(payload.getOverdueCasesCount());
        AuditSnapshot saved = repository.save(existing);
        log.info("Patched AuditSnapshot id={}", saved.getId());
        return saved;
    }

    public void delete(UUID id) {
        AuditSnapshot existing = findById(id);
        repository.delete(existing);
        log.info("Deleted AuditSnapshot id={}", id);
    }
}