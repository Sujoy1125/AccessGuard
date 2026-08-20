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

import com.accessguard.accessguard.entity.HighRiskActivityFlag;
import com.accessguard.accessguard.service.HighRiskActivityFlagService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/high-risk-activity-flags")
@Tag(name = "High-Risk Activity Flag", description = "Suspicious activity detected during offboarding (bulk downloads, exports)")
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