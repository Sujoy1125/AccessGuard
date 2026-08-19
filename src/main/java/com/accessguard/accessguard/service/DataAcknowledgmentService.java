package com.accessguard.accessguard.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.accessguard.accessguard.entity.DataAcknowledgment;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.DataAcknowledgmentRepository;

@Service
public class DataAcknowledgmentService {

    private final DataAcknowledgmentRepository repository;

    public DataAcknowledgmentService(DataAcknowledgmentRepository repository) {
        this.repository = repository;
    }

    public List<DataAcknowledgment> findAll() {
        return repository.findAll();
    }

    public DataAcknowledgment findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DataAcknowledgment", id));
    }

    public DataAcknowledgment create(DataAcknowledgment payload) {
        payload.setId(null); // never trust a client-supplied id on create
        return repository.save(payload);
    }

    // PUT: full replace. Every field in the request body overwrites the row.
    public DataAcknowledgment replace(UUID id, DataAcknowledgment payload) {
        DataAcknowledgment existing = findById(id);
        existing.setOffboardingCaseId(payload.getOffboardingCaseId());
        existing.setEmployeeId(payload.getEmployeeId());
        existing.setAcknowledgedAt(payload.getAcknowledgedAt());
        existing.setStatementVersion(payload.getStatementVersion());
        return repository.save(existing);
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
        return repository.save(existing);
    }

    public void delete(UUID id) {
        DataAcknowledgment existing = findById(id);
        repository.delete(existing);
    }
}