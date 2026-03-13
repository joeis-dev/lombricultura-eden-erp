package com.lombriculturaeden.controller;

import com.lombriculturaeden.dto.HarvestBatchRequest;
import com.lombriculturaeden.dto.HarvestBatchResponse;
import com.lombriculturaeden.service.HarvestBatchService;
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
@RequestMapping("/api/v1/harvest-batches")
@RequiredArgsConstructor
@Tag(name = "Harvest Batches", description = "Harvest batch management API")
public class HarvestBatchController {
    
    private final HarvestBatchService service;
    
    @GetMapping
    @Operation(summary = "Get all harvest batches")
    public ResponseEntity<List<HarvestBatchResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get harvest batch by ID")
    public ResponseEntity<HarvestBatchResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create a new harvest batch")
    public ResponseEntity<HarvestBatchResponse> create(@Valid @RequestBody HarvestBatchRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a harvest batch")
    public ResponseEntity<HarvestBatchResponse> update(@PathVariable UUID id, 
                                                        @Valid @RequestBody HarvestBatchRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a harvest batch")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
