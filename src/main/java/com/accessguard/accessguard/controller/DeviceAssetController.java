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

import com.accessguard.accessguard.entity.DeviceAsset;
import com.accessguard.accessguard.entity.Employee;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.DeviceAssetRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/device-assets")
@Tag(name = "DeviceAsset")
@Transactional
public class DeviceAssetController {
    private static final Logger log = LoggerFactory.getLogger(DeviceAssetController.class);
    private final DeviceAssetRepository repository;
    private final EntityManager entityManager;
    private final Validator validator;
    private final ObjectMapper mapper;

    public DeviceAssetController(DeviceAssetRepository repository, EntityManager entityManager, Validator validator,
            ObjectMapper mapper) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.validator = validator;
        this.mapper = mapper;
    }

    @GetMapping
    public List<DeviceAsset> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public DeviceAsset getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("DeviceAsset", id));
    }

    @PostMapping
    public ResponseEntity<DeviceAsset> create(@Valid @RequestBody DeviceAsset payload) {
        payload.setId(null);
        validate(payload);
        DeviceAsset saved = repository.saveAndFlush(payload);
        log.info("Created DeviceAsset {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public DeviceAsset replace(@PathVariable UUID id, @Valid @RequestBody DeviceAsset payload) {
        DeviceAsset existing = getById(id);
        validate(payload);
        copy(payload, existing);
        log.info("Replaced DeviceAsset {}", id);
        return repository.saveAndFlush(existing);
    }

    @PatchMapping("/{id}")
    public DeviceAsset partialUpdate(@PathVariable UUID id, @RequestBody ObjectNode patch) {
        DeviceAsset existing = getById(id);
        ObjectNode merged = (ObjectNode) mapper.valueToTree(existing);
        Set<String> allowed = Set.of("employeeId", "assetType", "returnStatus", "returnConfirmedAt");
        for (String field : patch.propertyNames()) {
            if (!allowed.contains(field))
                throw new IllegalArgumentException("Unknown or immutable field: " + field);
            merged.set(field, patch.get(field));
        }
        DeviceAsset payload = mapper.treeToValue(merged, DeviceAsset.class);
        validate(payload);
        copy(payload, existing);
        log.info("Patched DeviceAsset {}", id);
        return repository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        repository.delete(getById(id));
        repository.flush();
        log.info("Deleted DeviceAsset {}", id);
        return ResponseEntity.noContent().build();
    }

    private void copy(DeviceAsset payload, DeviceAsset existing) {
        existing.setEmployeeId(payload.getEmployeeId());
        existing.setAssetType(payload.getAssetType());
        existing.setReturnStatus(payload.getReturnStatus());
        existing.setReturnConfirmedAt(payload.getReturnConfirmedAt());
    }

    private void validate(DeviceAsset payload) {
        var errors = validator.validate(payload);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        if (payload.getEmployeeId() != null && entityManager.find(Employee.class, payload.getEmployeeId()) == null)
            throw new ResourceNotFoundException("Employee", payload.getEmployeeId());
    }
}
