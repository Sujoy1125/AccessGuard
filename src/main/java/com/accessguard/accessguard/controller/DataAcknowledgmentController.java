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

import com.accessguard.accessguard.entity.DataAcknowledgment;
import com.accessguard.accessguard.service.DataAcknowledgmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/data-acknowledgments")
@Tag(name = "Data Acknowledgment", description = "Employee sign-off on offboarding data-handling statements")
public class DataAcknowledgmentController {

    private final DataAcknowledgmentService service;

    public DataAcknowledgmentController(DataAcknowledgmentService service) {
        this.service = service;
    }

    @Operation(summary = "List all data acknowledgment records")
    @GetMapping
    public List<DataAcknowledgment> getAll() {
        return service.findAll();
    }

    @Operation(summary = "Get a data acknowledgment record by id")
    @GetMapping("/{id}")
    public DataAcknowledgment getById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @Operation(summary = "Create a new data acknowledgment record")
    @PostMapping
    public ResponseEntity<DataAcknowledgment> create(@Valid @RequestBody DataAcknowledgment payload) {
        DataAcknowledgment created = service.create(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Fully replace a data acknowledgment record")
    @PutMapping("/{id}")
    public DataAcknowledgment replace(@PathVariable UUID id, @Valid @RequestBody DataAcknowledgment payload) {
        return service.replace(id, payload);
    }

    @Operation(summary = "Partially update a data acknowledgment record")
    @PatchMapping("/{id}")
    public DataAcknowledgment partialUpdate(@PathVariable UUID id, @RequestBody DataAcknowledgment payload) {
        return service.partialUpdate(id, payload);
    }

    @Operation(summary = "Delete a data acknowledgment record")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}