package com.lombriculturaeden.controller;

import com.lombriculturaeden.dto.WormBedModificationRequest;
import com.lombriculturaeden.dto.WormBedModificationResponse;
import com.lombriculturaeden.service.WormBedModificationService;
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
@RequestMapping("/api/v1/worm-bed-modifications")
@RequiredArgsConstructor
@Tag(name = "Worm Bed Modifications", description = "Worm bed modification records API")
public class WormBedModificationController {
    
    private final WormBedModificationService service;
    
    @GetMapping
    @Operation(summary = "Get all worm bed modifications")
    public ResponseEntity<List<WormBedModificationResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get worm bed modification by ID")
    public ResponseEntity<WormBedModificationResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @GetMapping("/worm-bed/{wormBedId}")
    @Operation(summary = "Get modifications for a specific worm bed")
    public ResponseEntity<List<WormBedModificationResponse>> findByWormBedId(@PathVariable UUID wormBedId) {
        return ResponseEntity.ok(service.findByWormBedId(wormBedId));
    }
    
    @PostMapping
    @Operation(summary = "Create a new worm bed modification record")
    public ResponseEntity<WormBedModificationResponse> create(@Valid @RequestBody WormBedModificationRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a worm bed modification record")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
