package com.accessguard.accessguard.controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accessguard.accessguard.entity.Employee;
import com.accessguard.accessguard.entity.HighRiskActivityFlag;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.HighRiskActivityFlagRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/high-risk-activity-flags")
@Tag(name = "HighRiskActivityFlag")
@Transactional
public class HighRiskActivityFlagController {
    private static final Logger log = LoggerFactory.getLogger(HighRiskActivityFlagController.class);
    private final HighRiskActivityFlagRepository repository;
    private final EntityManager entityManager;
    private final Validator validator;
    private final ObjectMapper mapper;

    public HighRiskActivityFlagController(HighRiskActivityFlagRepository repository, EntityManager entityManager,
            Validator validator, ObjectMapper mapper) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.validator = validator;
        this.mapper = mapper;
    }

    @GetMapping
    public List<HighRiskActivityFlag> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public HighRiskActivityFlag getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("HighRiskActivityFlag", id));
    }

    @PostMapping
    public ResponseEntity<HighRiskActivityFlag> create(@Valid @RequestBody HighRiskActivityFlag payload) {
        payload.setId(null);
        validate(payload);
        HighRiskActivityFlag saved = repository.saveAndFlush(payload);
        log.info("Created HighRiskActivityFlag {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public HighRiskActivityFlag replace(@PathVariable UUID id, @Valid @RequestBody HighRiskActivityFlag payload) {
        HighRiskActivityFlag existing = getById(id);
        validate(payload);
        copy(payload, existing);
        log.info("Replaced HighRiskActivityFlag {}", id);
        return repository.saveAndFlush(existing);
    }

    @PatchMapping("/{id}")
    public HighRiskActivityFlag partialUpdate(@PathVariable UUID id, @RequestBody ObjectNode patch) {
        HighRiskActivityFlag existing = getById(id);
        ObjectNode merged = (ObjectNode) mapper.valueToTree(existing);
        Set<String> allowed = Set.of("employeeId", "sourceSystem", "activityType", "detectedAt", "reviewedBy",
                "reviewStatus", "detectedBy");
        for (String field : patch.propertyNames()) {
            if (!allowed.contains(field))
                throw new IllegalArgumentException("Unknown or immutable field: " + field);
            merged.set(field, patch.get(field));
        }
        HighRiskActivityFlag payload = mapper.treeToValue(merged, HighRiskActivityFlag.class);
        validate(payload);
        copy(payload, existing);
        log.info("Patched HighRiskActivityFlag {}", id);
        return repository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        repository.delete(getById(id));
        repository.flush();
        log.info("Deleted HighRiskActivityFlag {}", id);
        return ResponseEntity.noContent().build();
    }

    private void copy(HighRiskActivityFlag payload, HighRiskActivityFlag existing) {
        existing.setEmployeeId(payload.getEmployeeId());
        existing.setSourceSystem(payload.getSourceSystem());
        existing.setActivityType(payload.getActivityType());
        existing.setDetectedAt(payload.getDetectedAt());
        existing.setReviewedBy(payload.getReviewedBy());
        existing.setReviewStatus(payload.getReviewStatus());
        existing.setDetectedBy(payload.getDetectedBy());
    }

    private void validate(HighRiskActivityFlag payload) {
        var errors = validator.validate(payload);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        if (payload.getEmployeeId() != null && entityManager.find(Employee.class, payload.getEmployeeId()) == null)
            throw new ResourceNotFoundException("Employee", payload.getEmployeeId());
        if (payload.getReviewedBy() != null && entityManager.find(Employee.class, payload.getReviewedBy()) == null)
            throw new ResourceNotFoundException("Employee", payload.getReviewedBy());
        if (payload.getDetectedBy() != null && entityManager.find(Employee.class, payload.getDetectedBy()) == null)
            throw new ResourceNotFoundException("Employee", payload.getDetectedBy());
    }
}
