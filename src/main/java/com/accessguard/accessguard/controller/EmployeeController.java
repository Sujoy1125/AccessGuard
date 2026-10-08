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
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.EmployeeRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee")
@Transactional
public class EmployeeController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeRepository repository;
    private final EntityManager entityManager;
    private final Validator validator;
    private final ObjectMapper mapper;

    public EmployeeController(EmployeeRepository repository, EntityManager entityManager, Validator validator,
            ObjectMapper mapper) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.validator = validator;
        this.mapper = mapper;
    }

    @GetMapping
    public List<Employee> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }

    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee payload) {
        payload.setId(null);
        validate(payload);
        Employee saved = repository.saveAndFlush(payload);
        log.info("Created Employee {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public Employee replace(@PathVariable UUID id, @Valid @RequestBody Employee payload) {
        Employee existing = getById(id);
        validate(payload);
        copy(payload, existing);
        log.info("Replaced Employee {}", id);
        return repository.saveAndFlush(existing);
    }

    @PatchMapping("/{id}")
    public Employee partialUpdate(@PathVariable UUID id, @RequestBody ObjectNode patch) {
        Employee existing = getById(id);
        ObjectNode merged = (ObjectNode) mapper.valueToTree(existing);
        Set<String> allowed = Set.of("name", "email", "department", "status", "lastWorkingDay");
        for (String field : patch.propertyNames()) {
            if (!allowed.contains(field))
                throw new IllegalArgumentException("Unknown or immutable field: " + field);
            merged.set(field, patch.get(field));
        }
        Employee payload = mapper.treeToValue(merged, Employee.class);
        validate(payload);
        copy(payload, existing);
        log.info("Patched Employee {}", id);
        return repository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        getById(id);
        if (entityManager
                .createQuery("select count(g) from AccessGrantEntity g where g.employeeId = :id or g.grantedBy = :id",
                        Long.class)
                .setParameter("id", id).getSingleResult() > 0)
            throw new org.springframework.dao.DataIntegrityViolationException(
                    "Employee is referenced by access grants");
        repository.delete(getById(id));
        repository.flush();
        log.info("Deleted Employee {}", id);
        return ResponseEntity.noContent().build();
    }

    private void copy(Employee payload, Employee existing) {
        existing.setName(payload.getName());
        existing.setEmail(payload.getEmail());
        existing.setDepartment(payload.getDepartment());
        existing.setStatus(payload.getStatus());
        existing.setLastWorkingDay(payload.getLastWorkingDay());
    }

    private void validate(Employee payload) {
        var errors = validator.validate(payload);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
    }
}
