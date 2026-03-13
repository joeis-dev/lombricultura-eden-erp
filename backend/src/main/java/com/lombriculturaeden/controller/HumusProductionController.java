package com.lombriculturaeden.controller;

import com.lombriculturaeden.dto.HumusProductionRequest;
import com.lombriculturaeden.dto.HumusProductionResponse;
import com.lombriculturaeden.service.HumusProductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/humus-productions")
@RequiredArgsConstructor
@Tag(name = "Humus Productions", description = "Humus production management API")
public class HumusProductionController {
    
    private final HumusProductionService service;
    
    @GetMapping
    @Operation(summary = "Get all humus productions")
    public ResponseEntity<List<HumusProductionResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get humus production by ID")
    public ResponseEntity<HumusProductionResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @GetMapping("/batch/{batchId}")
    @Operation(summary = "Get humus productions by batch")
    public ResponseEntity<List<HumusProductionResponse>> findByBatch(@PathVariable UUID batchId) {
        return ResponseEntity.ok(service.findByBatch(batchId));
    }
    
    @PostMapping
    @Operation(summary = "Create a new humus production")
    public ResponseEntity<HumusProductionResponse> create(@Valid @RequestBody HumusProductionRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a humus production")
    public ResponseEntity<HumusProductionResponse> update(@PathVariable UUID id, 
                                                           @Valid @RequestBody HumusProductionRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a humus production")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
