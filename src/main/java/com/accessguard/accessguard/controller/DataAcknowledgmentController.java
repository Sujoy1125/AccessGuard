package com.accessguard.accessguard.controller;

import com.accessguard.accessguard.entity.DataAcknowledgment;
import com.accessguard.accessguard.service.DataAcknowledgmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/data-acknowledgments")
public class DataAcknowledgmentController {

    private final DataAcknowledgmentService service;

    public DataAcknowledgmentController(DataAcknowledgmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<DataAcknowledgment> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public DataAcknowledgment getById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<DataAcknowledgment> create(@Valid @RequestBody DataAcknowledgment payload) {
        DataAcknowledgment created = service.create(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public DataAcknowledgment replace(@PathVariable UUID id, @Valid @RequestBody DataAcknowledgment payload) {
        return service.replace(id, payload);
    }

    @PatchMapping("/{id}")
    public DataAcknowledgment partialUpdate(@PathVariable UUID id, @RequestBody DataAcknowledgment payload) {
        return service.partialUpdate(id, payload);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}