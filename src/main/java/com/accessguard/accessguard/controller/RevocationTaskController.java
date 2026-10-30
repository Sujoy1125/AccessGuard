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
import com.accessguard.accessguard.entity.OffboardingCase;
import com.accessguard.accessguard.entity.RevocationTask;
import com.accessguard.accessguard.entity.SystemEntity;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.RevocationTaskRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/revocation-tasks")
@Tag(name = "RevocationTask")
@Transactional
public class RevocationTaskController {
    private static final Logger log = LoggerFactory.getLogger(RevocationTaskController.class);
    private final RevocationTaskRepository repository;
    private final EntityManager entityManager;
    private final Validator validator;
    private final ObjectMapper mapper;

    public RevocationTaskController(RevocationTaskRepository repository, EntityManager entityManager,
            Validator validator, ObjectMapper mapper) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.validator = validator;
        this.mapper = mapper;
    }

    @GetMapping
    public List<RevocationTask> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public RevocationTask getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("RevocationTask", id));
    }

    @PostMapping
    public ResponseEntity<RevocationTask> create(@Valid @RequestBody RevocationTask payload) {
        payload.setId(null);
        validate(payload);
        RevocationTask saved = repository.saveAndFlush(payload);
        log.info("Created RevocationTask {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public RevocationTask replace(@PathVariable UUID id, @Valid @RequestBody RevocationTask payload) {
        RevocationTask existing = getById(id);
        validate(payload);
        copy(payload, existing);
        log.info("Replaced RevocationTask {}", id);
        return repository.saveAndFlush(existing);
    }

    @PatchMapping("/{id}")
    public RevocationTask partialUpdate(@PathVariable UUID id, @RequestBody ObjectNode patch) {
        RevocationTask existing = getById(id);
        ObjectNode merged = (ObjectNode) mapper.valueToTree(existing);
        Set<String> allowed = Set.of("offboardingCaseId", "systemId", "assignedOwnerId", "status", "confirmedAt",
                "confirmedBy");
        for (String field : patch.propertyNames()) {
            if (!allowed.contains(field))
                throw new IllegalArgumentException("Unknown or immutable field: " + field);
            merged.set(field, patch.get(field));
        }
        RevocationTask payload = mapper.treeToValue(merged, RevocationTask.class);
        validate(payload);
        copy(payload, existing);
        log.info("Patched RevocationTask {}", id);
        return repository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        repository.delete(getById(id));
        repository.flush();
        log.info("Deleted RevocationTask {}", id);
        return ResponseEntity.noContent().build();
    }

    private void copy(RevocationTask payload, RevocationTask existing) {
        existing.setOffboardingCaseId(payload.getOffboardingCaseId());
        existing.setSystemId(payload.getSystemId());
        existing.setAssignedOwnerId(payload.getAssignedOwnerId());
        existing.setStatus(payload.getStatus());
        existing.setConfirmedAt(payload.getConfirmedAt());
        existing.setConfirmedBy(payload.getConfirmedBy());
    }

    private void validate(RevocationTask payload) {
        var errors = validator.validate(payload);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        if (payload.getOffboardingCaseId() != null
                && entityManager.find(OffboardingCase.class, payload.getOffboardingCaseId()) == null)
            throw new ResourceNotFoundException("OffboardingCase", payload.getOffboardingCaseId());
        if (payload.getSystemId() != null && entityManager.find(SystemEntity.class, payload.getSystemId()) == null)
            throw new ResourceNotFoundException("SystemEntity", payload.getSystemId());
        if (payload.getAssignedOwnerId() != null
                && entityManager.find(Employee.class, payload.getAssignedOwnerId()) == null)
            throw new ResourceNotFoundException("Employee", payload.getAssignedOwnerId());
        if (payload.getConfirmedBy() != null && entityManager.find(Employee.class, payload.getConfirmedBy()) == null)
            throw new ResourceNotFoundException("Employee", payload.getConfirmedBy());
        if ((payload.getConfirmedAt() == null) != (payload.getConfirmedBy() == null))
            throw new IllegalArgumentException("confirmedAt and confirmedBy must be provided together");
    }
}
