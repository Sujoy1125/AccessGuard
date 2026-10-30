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

import com.accessguard.accessguard.entity.AccessGrantEntity;
import com.accessguard.accessguard.entity.DeviceAsset;
import com.accessguard.accessguard.entity.Employee;
import com.accessguard.accessguard.entity.SystemEntity;
import com.accessguard.accessguard.exception.ResourceNotFoundException;
import com.accessguard.accessguard.repository.AccessGrantRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/access-grants")
@Tag(name = "AccessGrant")
@Transactional
public class AccessGrantController {
    private static final Logger log = LoggerFactory.getLogger(AccessGrantController.class);
    private final AccessGrantRepository repository;
    private final EntityManager entityManager;
    private final Validator validator;
    private final ObjectMapper mapper;

    public AccessGrantController(AccessGrantRepository repository, EntityManager entityManager, Validator validator,
            ObjectMapper mapper) {
        this.repository = repository;
        this.entityManager = entityManager;
        this.validator = validator;
        this.mapper = mapper;
    }

    @GetMapping
    public List<AccessGrantEntity> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public AccessGrantEntity getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AccessGrantEntity", id));
    }

    @PostMapping
    public ResponseEntity<AccessGrantEntity> create(@Valid @RequestBody AccessGrantEntity payload) {
        payload.setId(null);
        validate(payload);
        AccessGrantEntity saved = repository.saveAndFlush(payload);
        log.info("Created AccessGrantEntity {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public AccessGrantEntity replace(@PathVariable UUID id, @Valid @RequestBody AccessGrantEntity payload) {
        AccessGrantEntity existing = getById(id);
        validate(payload);
        copy(payload, existing);
        log.info("Replaced AccessGrantEntity {}", id);
        return repository.saveAndFlush(existing);
    }

    @PatchMapping("/{id}")
    public AccessGrantEntity partialUpdate(@PathVariable UUID id, @RequestBody ObjectNode patch) {
        AccessGrantEntity existing = getById(id);
        ObjectNode merged = (ObjectNode) mapper.valueToTree(existing);
        Set<String> allowed = Set.of("employeeId", "systemId", "grantedAt", "grantedBy", "accessLevel",
                "deviceAssetId");
        for (String field : patch.propertyNames()) {
            if (!allowed.contains(field))
                throw new IllegalArgumentException("Unknown or immutable field: " + field);
            merged.set(field, patch.get(field));
        }
        AccessGrantEntity payload = mapper.treeToValue(merged, AccessGrantEntity.class);
        validate(payload);
        copy(payload, existing);
        log.info("Patched AccessGrantEntity {}", id);
        return repository.saveAndFlush(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        repository.delete(getById(id));
        repository.flush();
        log.info("Deleted AccessGrantEntity {}", id);
        return ResponseEntity.noContent().build();
    }

    private void copy(AccessGrantEntity payload, AccessGrantEntity existing) {
        existing.setEmployeeId(payload.getEmployeeId());
        existing.setSystemId(payload.getSystemId());
        existing.setGrantedAt(payload.getGrantedAt());
        existing.setGrantedBy(payload.getGrantedBy());
        existing.setAccessLevel(payload.getAccessLevel());
        existing.setDeviceAssetId(payload.getDeviceAssetId());
    }

    private void validate(AccessGrantEntity payload) {
        var errors = validator.validate(payload);
        if (!errors.isEmpty())
            throw new ConstraintViolationException(errors);
        if (payload.getDeviceAssetId() != null) {
            DeviceAsset asset = entityManager.find(DeviceAsset.class, payload.getDeviceAssetId());
            if (asset != null && !asset.getEmployeeId().equals(payload.getEmployeeId()))
                throw new IllegalArgumentException("Device asset must belong to the granted employee");
        }
        if (payload.getEmployeeId() != null && entityManager.find(Employee.class, payload.getEmployeeId()) == null)
            throw new ResourceNotFoundException("Employee", payload.getEmployeeId());
        if (payload.getSystemId() != null && entityManager.find(SystemEntity.class, payload.getSystemId()) == null)
            throw new ResourceNotFoundException("SystemEntity", payload.getSystemId());
        if (payload.getGrantedBy() != null && entityManager.find(Employee.class, payload.getGrantedBy()) == null)
            throw new ResourceNotFoundException("Employee", payload.getGrantedBy());
        if (payload.getDeviceAssetId() != null
                && entityManager.find(DeviceAsset.class, payload.getDeviceAssetId()) == null)
            throw new ResourceNotFoundException("DeviceAsset", payload.getDeviceAssetId());
    }
}
