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

import com.accessguard.accessguard.entity.DataAcknowledgment;
import com.accessguard.accessguard.entity.Employee;
import com.accessguard.accessguard.entity.OffboardingCase;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.DataAcknowledgmentRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/data-acknowledgments")
@Tag(name = "DataAcknowledgment")
@Transactional
public class DataAcknowledgmentController {
    private static final Logger log = LoggerFactory.getLogger(DataAcknowledgmentController.class);
    private final DataAcknowledgmentRepository repository;
    private final EntityManager entityManager;
    private final Validator validator;
    private final ObjectMapper mapper;

    public DataAcknowledgmentController(DataAcknowledgmentRepository repository, EntityManager entityManager,
            Validator validator, ObjectMapper mapper) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.validator = validator;
        this.mapper = mapper;
    }

    @GetMapping
    public List<DataAcknowledgment> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public DataAcknowledgment getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DataAcknowledgment", id));
    }

    @PostMapping
    public ResponseEntity<DataAcknowledgment> create(@Valid @RequestBody DataAcknowledgment payload) {
        payload.setId(null);
        validate(payload);
        DataAcknowledgment saved = repository.saveAndFlush(payload);
        log.info("Created DataAcknowledgment {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public DataAcknowledgment replace(@PathVariable UUID id, @Valid @RequestBody DataAcknowledgment payload) {
        DataAcknowledgment existing = getById(id);
        validate(payload);
        copy(payload, existing);
        log.info("Replaced DataAcknowledgment {}", id);
        return repository.saveAndFlush(existing);
    }

    @PatchMapping("/{id}")
    public DataAcknowledgment partialUpdate(@PathVariable UUID id, @RequestBody ObjectNode patch) {
        DataAcknowledgment existing = getById(id);
        ObjectNode merged = (ObjectNode) mapper.valueToTree(existing);
        Set<String> allowed = Set.of("offboardingCaseId", "employeeId", "acknowledgedAt", "statementVersion");
        for (String field : patch.propertyNames()) {
            if (!allowed.contains(field))
                throw new IllegalArgumentException("Unknown or immutable field: " + field);
            merged.set(field, patch.get(field));
        }
        DataAcknowledgment payload = mapper.treeToValue(merged, DataAcknowledgment.class);
        validate(payload);
        copy(payload, existing);
        log.info("Patched DataAcknowledgment {}", id);
        return repository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        repository.delete(getById(id));
        repository.flush();
        log.info("Deleted DataAcknowledgment {}", id);
        return ResponseEntity.noContent().build();
    }

    private void copy(DataAcknowledgment payload, DataAcknowledgment existing) {
        existing.setOffboardingCaseId(payload.getOffboardingCaseId());
        existing.setEmployeeId(payload.getEmployeeId());
        existing.setAcknowledgedAt(payload.getAcknowledgedAt());
        existing.setStatementVersion(payload.getStatementVersion());
    }

    private void validate(DataAcknowledgment payload) {
        var errors = validator.validate(payload);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        if (payload.getEmployeeId() != null && entityManager.find(Employee.class, payload.getEmployeeId()) == null)
            throw new ResourceNotFoundException("Employee", payload.getEmployeeId());
        if (payload.getOffboardingCaseId() != null
                && entityManager.find(OffboardingCase.class, payload.getOffboardingCaseId()) == null)
            throw new ResourceNotFoundException("OffboardingCase", payload.getOffboardingCaseId());
        if (!entityManager.find(OffboardingCase.class, payload.getOffboardingCaseId()).getEmployeeId()
                .equals(payload.getEmployeeId()))
            throw new IllegalArgumentException("Acknowledgment employee must match offboarding case");
    }
}
