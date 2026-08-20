package com.accessguard.accessguard.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.accessguard.accessguard.entity.DataAcknowledgment;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.DataAcknowledgmentRepository;

@Service
public class DataAcknowledgmentService {

    private static final Logger log = LoggerFactory.getLogger(DataAcknowledgmentService.class);

    private final DataAcknowledgmentRepository repository;

    public DataAcknowledgmentService(DataAcknowledgmentRepository repository) {
        this.repository = repository;
    }

    public List<DataAcknowledgment> findAll() {
        log.debug("Fetching all DataAcknowledgment records");
        List<DataAcknowledgment> results = repository.findAll();
        log.info("Fetched {} DataAcknowledgment records", results.size());
        return results;
    }

    public DataAcknowledgment findById(UUID id) {
        log.debug("Looking up DataAcknowledgment id={}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("DataAcknowledgment not found id={}", id);
                    return new ResourceNotFoundException("DataAcknowledgment", id);
                });
    }

    public DataAcknowledgment create(DataAcknowledgment payload) {
        payload.setId(null); // never trust a client-supplied id on create
        DataAcknowledgment saved = repository.save(payload);
        log.info("Created DataAcknowledgment id={} employeeId={}", saved.getId(), saved.getEmployeeId());
        return saved;
    }

    // PUT: full replace. Every field in the request body overwrites the row.
    public DataAcknowledgment replace(UUID id, DataAcknowledgment payload) {
        DataAcknowledgment existing = findById(id);
        existing.setOffboardingCaseId(payload.getOffboardingCaseId());
        existing.setEmployeeId(payload.getEmployeeId());
        existing.setAcknowledgedAt(payload.getAcknowledgedAt());
        existing.setStatementVersion(payload.getStatementVersion());
        DataAcknowledgment saved = repository.save(existing);
        log.info("Replaced DataAcknowledgment id={}", saved.getId());
        return saved;
    }

    // PATCH: partial update. Only non-null fields in the payload are applied.
    public DataAcknowledgment partialUpdate(UUID id, DataAcknowledgment payload) {
        DataAcknowledgment existing = findById(id);
        if (payload.getOffboardingCaseId() != null) {
            existing.setOffboardingCaseId(payload.getOffboardingCaseId());
        }
        if (payload.getEmployeeId() != null) {
            existing.setEmployeeId(payload.getEmployeeId());
        }
        if (payload.getAcknowledgedAt() != null) {
            existing.setAcknowledgedAt(payload.getAcknowledgedAt());
        }
        if (payload.getStatementVersion() != null) {
            existing.setStatementVersion(payload.getStatementVersion());
        }
        DataAcknowledgment saved = repository.save(existing);
        log.info("Patched DataAcknowledgment id={}", saved.getId());
        return saved;
    }

    public void delete(UUID id) {
        DataAcknowledgment existing = findById(id);
        repository.delete(existing);
        log.info("Deleted DataAcknowledgment id={}", id);
    }
}