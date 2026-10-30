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
import com.accessguard.accessguard.entity.OffboardingCase;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.OffboardingCaseRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/offboarding-cases")
@Tag(name = "OffboardingCase")
@Transactional
public class OffboardingCaseController {
    private static final Logger log = LoggerFactory.getLogger(OffboardingCaseController.class);
    private final OffboardingCaseRepository repository;
    private final EntityManager entityManager;
    private final Validator validator;
    private final ObjectMapper mapper;

    public OffboardingCaseController(OffboardingCaseRepository repository, EntityManager entityManager,
            Validator validator, ObjectMapper mapper) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.validator = validator;
        this.mapper = mapper;
    }

    @GetMapping
    public List<OffboardingCase> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public OffboardingCase getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("OffboardingCase", id));
    }

    @PostMapping
    public ResponseEntity<OffboardingCase> create(@Valid @RequestBody OffboardingCase payload) {
        payload.setId(null);
        validate(payload);
        OffboardingCase saved = repository.saveAndFlush(payload);
        log.info("Created OffboardingCase {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public OffboardingCase replace(@PathVariable UUID id, @Valid @RequestBody OffboardingCase payload) {
        OffboardingCase existing = getById(id);
        validate(payload);
        copy(payload, existing);
        log.info("Replaced OffboardingCase {}", id);
        return repository.saveAndFlush(existing);
    }

    @PatchMapping("/{id}")
    public OffboardingCase partialUpdate(@PathVariable UUID id, @RequestBody ObjectNode patch) {
        OffboardingCase existing = getById(id);
        ObjectNode merged = (ObjectNode) mapper.valueToTree(existing);
        Set<String> allowed = Set.of("employeeId", "initiatedBy", "initiatedAt", "targetCompletionDate", "status",
                "triggerFlagId");
        for (String field : patch.propertyNames()) {
            if (!allowed.contains(field))
                throw new IllegalArgumentException("Unknown or immutable field: " + field);
            merged.set(field, patch.get(field));
        }
        OffboardingCase payload = mapper.treeToValue(merged, OffboardingCase.class);
        validate(payload);
        copy(payload, existing);
        log.info("Patched OffboardingCase {}", id);
        return repository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        repository.delete(getById(id));
        repository.flush();
        log.info("Deleted OffboardingCase {}", id);
        return ResponseEntity.noContent().build();
    }

    private void copy(OffboardingCase payload, OffboardingCase existing) {
        existing.setEmployeeId(payload.getEmployeeId());
        existing.setInitiatedBy(payload.getInitiatedBy());
        existing.setInitiatedAt(payload.getInitiatedAt());
        existing.setTargetCompletionDate(payload.getTargetCompletionDate());
        existing.setStatus(payload.getStatus());
        existing.setTriggerFlagId(payload.getTriggerFlagId());
    }

    private void validate(OffboardingCase payload) {
        var errors = validator.validate(payload);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        if (payload.getTriggerFlagId() != null) {
            HighRiskActivityFlag flag = entityManager.find(HighRiskActivityFlag.class, payload.getTriggerFlagId());
            if (flag != null && !flag.getEmployeeId().equals(payload.getEmployeeId()))
                throw new IllegalArgumentException("Trigger flag must belong to the offboarding employee");
        }
        if (payload.getEmployeeId() != null && entityManager.find(Employee.class, payload.getEmployeeId()) == null)
            throw new ResourceNotFoundException("Employee", payload.getEmployeeId());
        if (payload.getInitiatedBy() != null && entityManager.find(Employee.class, payload.getInitiatedBy()) == null)
            throw new ResourceNotFoundException("Employee", payload.getInitiatedBy());
        if (payload.getTriggerFlagId() != null
                && entityManager.find(HighRiskActivityFlag.class, payload.getTriggerFlagId()) == null)
            throw new ResourceNotFoundException("HighRiskActivityFlag", payload.getTriggerFlagId());
        if (payload.getTargetCompletionDate().isBefore(payload.getInitiatedAt().toLocalDate()))
            throw new IllegalArgumentException("Target completion date must not precede initiation");
    }
}
