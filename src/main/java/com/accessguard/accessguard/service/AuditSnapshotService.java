package com.accessguard.accessguard.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.accessguard.accessguard.entity.AuditSnapshot;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.AuditSnapshotRepository;

@Service
public class AuditSnapshotService {

    private final AuditSnapshotRepository repository;

    public AuditSnapshotService(AuditSnapshotRepository repository) {
        this.repository = repository;
    }

    public List<AuditSnapshot> findAll() {
        return repository.findAll();
    }

    public AuditSnapshot findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditSnapshot", id));
    }

    public AuditSnapshot create(AuditSnapshot payload) {
        payload.setId(null);
        return repository.save(payload);
    }

    public AuditSnapshot replace(UUID id, AuditSnapshot payload) {
        AuditSnapshot existing = findById(id);
        existing.setPeriodStart(payload.getPeriodStart());
        existing.setPeriodEnd(payload.getPeriodEnd());
        existing.setTotalOffboardings(payload.getTotalOffboardings());
        existing.setPctFullyRevokedOnTime(payload.getPctFullyRevokedOnTime());
        existing.setAvgRevocationTimeHrs(payload.getAvgRevocationTimeHrs());
        existing.setOverdueCasesCount(payload.getOverdueCasesCount());
        return repository.save(existing);
    }

    public AuditSnapshot partialUpdate(UUID id, AuditSnapshot payload) {
        AuditSnapshot existing = findById(id);
        if (payload.getPeriodStart() != null) {
            existing.setPeriodStart(payload.getPeriodStart());
        }
        if (payload.getPeriodEnd() != null) {
            existing.setPeriodEnd(payload.getPeriodEnd());
        }
        if (payload.getTotalOffboardings() != null) {
            existing.setTotalOffboardings(payload.getTotalOffboardings());
        }
        if (payload.getPctFullyRevokedOnTime() != null) {
            existing.setPctFullyRevokedOnTime(payload.getPctFullyRevokedOnTime());
        }
        if (payload.getAvgRevocationTimeHrs() != null) {
            existing.setAvgRevocationTimeHrs(payload.getAvgRevocationTimeHrs());
        }
        if (payload.getOverdueCasesCount() != null) {
            existing.setOverdueCasesCount(payload.getOverdueCasesCount());
        }
        return repository.save(existing);
    }

    public void delete(UUID id) {
        AuditSnapshot existing = findById(id);
        repository.delete(existing);
    }
}