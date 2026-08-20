package com.accessguard.accessguard.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.accessguard.accessguard.entity.HighRiskActivityFlag;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.HighRiskActivityFlagRepository;

@Service
public class HighRiskActivityFlagService {

    private static final Logger log = LoggerFactory.getLogger(HighRiskActivityFlagService.class);

    private final HighRiskActivityFlagRepository repository;

    public HighRiskActivityFlagService(HighRiskActivityFlagRepository repository) {
        this.repository = repository;
    }

    public List<HighRiskActivityFlag> findAll() {
        log.debug("Fetching all HighRiskActivityFlag records");
        return repository.findAll();
    }

    public HighRiskActivityFlag findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("HighRiskActivityFlag not found id={}", id);
                    return new ResourceNotFoundException("HighRiskActivityFlag", id);
                });
    }

    public HighRiskActivityFlag create(HighRiskActivityFlag payload) {
        payload.setId(null);
        HighRiskActivityFlag saved = repository.save(payload);
        log.info("Created HighRiskActivityFlag id={} employeeId={} activityType={}",
                saved.getId(), saved.getEmployeeId(), saved.getActivityType());
        return saved;
    }

    public HighRiskActivityFlag replace(UUID id, HighRiskActivityFlag payload) {
        HighRiskActivityFlag existing = findById(id);
        existing.setEmployeeId(payload.getEmployeeId());
        existing.setSourceSystem(payload.getSourceSystem());
        existing.setActivityType(payload.getActivityType());
        existing.setDetectedAt(payload.getDetectedAt());
        existing.setReviewedBy(payload.getReviewedBy());
        existing.setReviewStatus(payload.getReviewStatus());
        HighRiskActivityFlag saved = repository.save(existing);
        log.info("Replaced HighRiskActivityFlag id={}", saved.getId());
        return saved;
    }

    public HighRiskActivityFlag partialUpdate(UUID id, HighRiskActivityFlag payload) {
        HighRiskActivityFlag existing = findById(id);
        if (payload.getEmployeeId() != null)
            existing.setEmployeeId(payload.getEmployeeId());
        if (payload.getSourceSystem() != null)
            existing.setSourceSystem(payload.getSourceSystem());
        if (payload.getActivityType() != null)
            existing.setActivityType(payload.getActivityType());
        if (payload.getDetectedAt() != null)
            existing.setDetectedAt(payload.getDetectedAt());
        if (payload.getReviewedBy() != null)
            existing.setReviewedBy(payload.getReviewedBy());
        if (payload.getReviewStatus() != null)
            existing.setReviewStatus(payload.getReviewStatus());
        HighRiskActivityFlag saved = repository.save(existing);
        log.info("Patched HighRiskActivityFlag id={} reviewStatus={}", saved.getId(), saved.getReviewStatus());
        return saved;
    }

    public void delete(UUID id) {
        HighRiskActivityFlag existing = findById(id);
        repository.delete(existing);
        log.info("Deleted HighRiskActivityFlag id={}", id);
    }
}