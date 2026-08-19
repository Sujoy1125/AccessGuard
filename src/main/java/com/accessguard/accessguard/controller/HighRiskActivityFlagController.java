package com.accessguard.accessguard.controller;

import com.accessguard.accessguard.entity.HighRiskActivityFlag;
import com.accessguard.accessguard.service.HighRiskActivityFlagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/high-risk-activity-flags")
public class HighRiskActivityFlagController {

    private final HighRiskActivityFlagService service;

    public HighRiskActivityFlagController(HighRiskActivityFlagService service) {
        this.service = service;
    }

    @GetMapping
    public List<HighRiskActivityFlag> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public HighRiskActivityFlag getById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<HighRiskActivityFlag> create(@Valid @RequestBody HighRiskActivityFlag payload) {
        HighRiskActivityFlag created = service.create(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public HighRiskActivityFlag replace(@PathVariable UUID id, @Valid @RequestBody HighRiskActivityFlag payload) {
        return service.replace(id, payload);
    }

    // PATCH is the realistic path here: a reviewer sets reviewedBy + reviewStatus
    @PatchMapping("/{id}")
    public HighRiskActivityFlag partialUpdate(@PathVariable UUID id, @RequestBody HighRiskActivityFlag payload) {
        return service.partialUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}