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
import com.accessguard.accessguard.entity.EscalationLog;
import com.accessguard.accessguard.entity.RevocationTask;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.EscalationLogRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/escalation-logs")
@Tag(name = "EscalationLog")
@Transactional
public class EscalationLogController {
    private static final Logger log = LoggerFactory.getLogger(EscalationLogController.class);
    private final EscalationLogRepository repository;
    private final EntityManager entityManager;
    private final Validator validator;
    private final ObjectMapper mapper;

    public EscalationLogController(EscalationLogRepository repository, EntityManager entityManager, Validator validator,
            ObjectMapper mapper) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.validator = validator;
        this.mapper = mapper;
    }

    @GetMapping
    public List<EscalationLog> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public EscalationLog getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("EscalationLog", id));
    }

    @PostMapping
    public ResponseEntity<EscalationLog> create(@Valid @RequestBody EscalationLog payload) {
        payload.setId(null);
        validate(payload);
        EscalationLog saved = repository.saveAndFlush(payload);
        log.info("Created EscalationLog {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public EscalationLog replace(@PathVariable UUID id, @Valid @RequestBody EscalationLog payload) {
        EscalationLog existing = getById(id);
        validate(payload);
        copy(payload, existing);
        log.info("Replaced EscalationLog {}", id);
        return repository.saveAndFlush(existing);
    }

    @PatchMapping("/{id}")
    public EscalationLog partialUpdate(@PathVariable UUID id, @RequestBody ObjectNode patch) {
        EscalationLog existing = getById(id);
        ObjectNode merged = (ObjectNode) mapper.valueToTree(existing);
        Set<String> allowed = Set.of("revocationTaskId", "escalatedTo", "escalatedAt", "reason");
        for (String field : patch.propertyNames()) {
            if (!allowed.contains(field))
                throw new IllegalArgumentException("Unknown or immutable field: " + field);
            merged.set(field, patch.get(field));
        }
        EscalationLog payload = mapper.treeToValue(merged, EscalationLog.class);
        validate(payload);
        copy(payload, existing);
        log.info("Patched EscalationLog {}", id);
        return repository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        repository.delete(getById(id));
        repository.flush();
        log.info("Deleted EscalationLog {}", id);
        return ResponseEntity.noContent().build();
    }

    private void copy(EscalationLog payload, EscalationLog existing) {
        existing.setRevocationTaskId(payload.getRevocationTaskId());
        existing.setEscalatedTo(payload.getEscalatedTo());
        existing.setEscalatedAt(payload.getEscalatedAt());
        existing.setReason(payload.getReason());
    }

    private void validate(EscalationLog payload) {
        var errors = validator.validate(payload);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        if (payload.getRevocationTaskId() != null
                && entityManager.find(RevocationTask.class, payload.getRevocationTaskId()) == null)
            throw new ResourceNotFoundException("RevocationTask", payload.getRevocationTaskId());
        if (payload.getEscalatedTo() != null && entityManager.find(Employee.class, payload.getEscalatedTo()) == null)
            throw new ResourceNotFoundException("Employee", payload.getEscalatedTo());
    }
}
