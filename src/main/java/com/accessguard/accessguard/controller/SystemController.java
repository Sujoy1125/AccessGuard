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
import com.accessguard.accessguard.entity.SystemEntity;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.SystemRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/systems")
@Tag(name = "System")
@Transactional
public class SystemController {
    private static final Logger log = LoggerFactory.getLogger(SystemController.class);
    private final SystemRepository repository;
    private final EntityManager entityManager;
    private final Validator validator;
    private final ObjectMapper mapper;

    public SystemController(SystemRepository repository, EntityManager entityManager, Validator validator,
            ObjectMapper mapper) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.validator = validator;
        this.mapper = mapper;
    }

    @GetMapping
    public List<SystemEntity> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public SystemEntity getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SystemEntity", id));
    }

    @PostMapping
    public ResponseEntity<SystemEntity> create(@Valid @RequestBody SystemEntity payload) {
        payload.setId(null);
        validate(payload);
        SystemEntity saved = repository.saveAndFlush(payload);
        log.info("Created SystemEntity {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public SystemEntity replace(@PathVariable UUID id, @Valid @RequestBody SystemEntity payload) {
        SystemEntity existing = getById(id);
        validate(payload);
        copy(payload, existing);
        log.info("Replaced SystemEntity {}", id);
        return repository.saveAndFlush(existing);
    }

    @PatchMapping("/{id}")
    public SystemEntity partialUpdate(@PathVariable UUID id, @RequestBody ObjectNode patch) {
        SystemEntity existing = getById(id);
        ObjectNode merged = (ObjectNode) mapper.valueToTree(existing);
        Set<String> allowed = Set.of("name", "ownerUserId", "category");
        for (String field : patch.propertyNames()) {
            if (!allowed.contains(field))
                throw new IllegalArgumentException("Unknown or immutable field: " + field);
            merged.set(field, patch.get(field));
        }
        SystemEntity payload = mapper.treeToValue(merged, SystemEntity.class);
        validate(payload);
        copy(payload, existing);
        log.info("Patched SystemEntity {}", id);
        return repository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        getById(id);
        if (entityManager.createQuery("select count(g) from AccessGrantEntity g where g.systemId = :id", Long.class)
                .setParameter("id", id).getSingleResult() > 0)
            throw new org.springframework.dao.DataIntegrityViolationException("System is referenced by access grants");
        repository.delete(getById(id));
        repository.flush();
        log.info("Deleted SystemEntity {}", id);
        return ResponseEntity.noContent().build();
    }

    private void copy(SystemEntity payload, SystemEntity existing) {
        existing.setName(payload.getName());
        existing.setOwnerUserId(payload.getOwnerUserId());
        existing.setCategory(payload.getCategory());
    }

    private void validate(SystemEntity payload) {
        var errors = validator.validate(payload);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        if (payload.getOwnerUserId() != null && entityManager.find(Employee.class, payload.getOwnerUserId()) == null)
            throw new ResourceNotFoundException("Employee", payload.getOwnerUserId());
    }
}
