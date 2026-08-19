package com.accessguard.accessguard.controller;

import com.accessguard.accessguard.entity.EscalationLog;
import com.accessguard.accessguard.service.EscalationLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/escalation-logs")
public class EscalationLogController {

    private final EscalationLogService service;

    public EscalationLogController(EscalationLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<EscalationLog> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public EscalationLog getById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<EscalationLog> create(@Valid @RequestBody EscalationLog payload) {
        EscalationLog created = service.create(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public EscalationLog replace(@PathVariable UUID id, @Valid @RequestBody EscalationLog payload) {
        return service.replace(id, payload);
    }

    @PatchMapping("/{id}")
    public EscalationLog partialUpdate(@PathVariable UUID id, @RequestBody EscalationLog payload) {
        return service.partialUpdate(id, payload);
    }

    // Kept for CRUD completeness / testing today. Consider removing before
    // the real demo — EscalationLog is meant to be an append-only audit trail.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}