package com.accessguard.accessguard.service;

import com.accessguard.accessguard.entity.HighRiskActivityFlag;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.HighRiskActivityFlagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HighRiskActivityFlagService {

    private final HighRiskActivityFlagRepository repository;

    public HighRiskActivityFlagService(HighRiskActivityFlagRepository repository) {
        this.repository = repository;
    }

    public List<HighRiskActivityFlag> findAll() {
        return repository.findAll();
    }

    public HighRiskActivityFlag findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HighRiskActivityFlag", id));
    }

    public HighRiskActivityFlag create(HighRiskActivityFlag payload) {
        payload.setId(null);
        if (payload.getReviewStatus() == null) {
            payload.setReviewStatus("PENDING");
        }
        return repository.save(payload);
    }

    public HighRiskActivityFlag replace(UUID id, HighRiskActivityFlag payload) {
        HighRiskActivityFlag existing = findById(id);
        existing.setEmployeeId(payload.getEmployeeId());
        existing.setSourceSystem(payload.getSourceSystem());
        existing.setActivityType(payload.getActivityType());
        existing.setDetectedAt(payload.getDetectedAt());
        existing.setReviewedBy(payload.getReviewedBy());
        existing.setReviewStatus(payload.getReviewStatus());
        return repository.save(existing);
    }

    // PATCH is the realistic use case here: a reviewer picks up a flag and
    // sets reviewedBy + reviewStatus without resending the whole record.
    public HighRiskActivityFlag partialUpdate(UUID id, HighRiskActivityFlag payload) {
        HighRiskActivityFlag existing = findById(id);
        if (payload.getEmployeeId() != null) {
            existing.setEmployeeId(payload.getEmployeeId());
        }
        if (payload.getSourceSystem() != null) {
            existing.setSourceSystem(payload.getSourceSystem());
        }
        if (payload.getActivityType() != null) {
            existing.setActivityType(payload.getActivityType());
        }
        if (payload.getDetectedAt() != null) {
            existing.setDetectedAt(payload.getDetectedAt());
        }
        if (payload.getReviewedBy() != null) {
            existing.setReviewedBy(payload.getReviewedBy());
        }
        if (payload.getReviewStatus() != null) {
            existing.setReviewStatus(payload.getReviewStatus());
        }
        return repository.save(existing);
    }

    public void delete(UUID id) {
        HighRiskActivityFlag existing = findById(id);
        repository.delete(existing);
    }
}