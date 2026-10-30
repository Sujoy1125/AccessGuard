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

import com.accessguard.accessguard.entity.AuditSnapshot;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.AuditSnapshotRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/audit-snapshots")
@Tag(name = "AuditSnapshot")
@Transactional
public class AuditSnapshotController {
    private static final Logger log = LoggerFactory.getLogger(AuditSnapshotController.class);
    private final AuditSnapshotRepository repository;
    private final EntityManager entityManager;
    private final Validator validator;
    private final ObjectMapper mapper;

    public AuditSnapshotController(AuditSnapshotRepository repository, EntityManager entityManager, Validator validator,
            ObjectMapper mapper) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.validator = validator;
        this.mapper = mapper;
    }

    @GetMapping
    public List<AuditSnapshot> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public AuditSnapshot getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AuditSnapshot", id));
    }

    @PostMapping
    public ResponseEntity<AuditSnapshot> create(@Valid @RequestBody AuditSnapshot payload) {
        payload.setId(null);
        validate(payload);
        AuditSnapshot saved = repository.saveAndFlush(payload);
        log.info("Created AuditSnapshot {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public AuditSnapshot replace(@PathVariable UUID id, @Valid @RequestBody AuditSnapshot payload) {
        AuditSnapshot existing = getById(id);
        validate(payload);
        copy(payload, existing);
        log.info("Replaced AuditSnapshot {}", id);
        return repository.saveAndFlush(existing);
    }

    @PatchMapping("/{id}")
    public AuditSnapshot partialUpdate(@PathVariable UUID id, @RequestBody ObjectNode patch) {
        AuditSnapshot existing = getById(id);
        ObjectNode merged = (ObjectNode) mapper.valueToTree(existing);
        Set<String> allowed = Set.of("periodStart", "periodEnd", "totalOffboardings", "pctFullyRevokedOnTime",
                "avgRevocationTimeHrs", "overdueCasesCount", "retainedAssetCount");
        for (String field : patch.propertyNames()) {
            if (!allowed.contains(field))
                throw new IllegalArgumentException("Unknown or immutable field: " + field);
            merged.set(field, patch.get(field));
        }
        AuditSnapshot payload = mapper.treeToValue(merged, AuditSnapshot.class);
        validate(payload);
        copy(payload, existing);
        log.info("Patched AuditSnapshot {}", id);
        return repository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        repository.delete(getById(id));
        repository.flush();
        log.info("Deleted AuditSnapshot {}", id);
        return ResponseEntity.noContent().build();
    }

    private void copy(AuditSnapshot payload, AuditSnapshot existing) {
        existing.setPeriodStart(payload.getPeriodStart());
        existing.setPeriodEnd(payload.getPeriodEnd());
        existing.setTotalOffboardings(payload.getTotalOffboardings());
        existing.setPctFullyRevokedOnTime(payload.getPctFullyRevokedOnTime());
        existing.setAvgRevocationTimeHrs(payload.getAvgRevocationTimeHrs());
        existing.setOverdueCasesCount(payload.getOverdueCasesCount());
        existing.setRetainedAssetCount(payload.getRetainedAssetCount());
    }

    private void validate(AuditSnapshot payload) {
        var errors = validator.validate(payload);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        if (payload.getPeriodEnd().isBefore(payload.getPeriodStart()))
            throw new IllegalArgumentException("periodEnd must not precede periodStart");
    }
}
