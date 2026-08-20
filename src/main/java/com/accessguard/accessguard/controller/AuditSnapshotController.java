package com.accessguard.accessguard.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.accessguard.accessguard.service.AuditSnapshotService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/audit-snapshots")
@Tag(name = "Audit Snapshot", description = "Periodic compliance metrics — offboarding volume, on-time revocation rate, overdue cases")
public class AuditSnapshotController {

    private final AuditSnapshotService service;

    public AuditSnapshotController(AuditSnapshotService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuditSnapshot> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AuditSnapshot getById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<AuditSnapshot> create(@Valid @RequestBody AuditSnapshot payload) {
        AuditSnapshot created = service.create(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public AuditSnapshot replace(@PathVariable UUID id, @Valid @RequestBody AuditSnapshot payload) {
        return service.replace(id, payload);
    }

    @PatchMapping("/{id}")
    public AuditSnapshot partialUpdate(@PathVariable UUID id, @RequestBody AuditSnapshot payload) {
        return service.partialUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}