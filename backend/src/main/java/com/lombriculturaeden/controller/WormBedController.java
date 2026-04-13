package com.lombriculturaeden.controller;

import com.lombriculturaeden.dto.WormBedRequest;
import com.lombriculturaeden.dto.WormBedResponse;
import com.lombriculturaeden.service.WormBedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/worm-beds")
@RequiredArgsConstructor
@Tag(name = "Worm Beds", description = "Worm bed management API")
public class WormBedController {
    
    private final WormBedService service;
    
    @GetMapping
    @Operation(summary = "Get all worm beds")
    public ResponseEntity<List<WormBedResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get worm bed by ID")
    public ResponseEntity<WormBedResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create a new worm bed")
    public ResponseEntity<WormBedResponse> create(@Valid @RequestBody WormBedRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a worm bed")
    public ResponseEntity<WormBedResponse> update(@PathVariable UUID id, 
                                                  @Valid @RequestBody WormBedRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a worm bed")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/subtraction-date")
    @Operation(summary = "Update last subtraction date")
    public ResponseEntity<WormBedResponse> updateSubtractionDate(@PathVariable UUID id,
                                                                  @RequestParam LocalDate date) {
        return ResponseEntity.ok(service.updateSubtractionDate(id, date));
    }
    
    @PatchMapping("/{id}/approximate-count")
    @Operation(summary = "Update approximate worm count")
    public ResponseEntity<WormBedResponse> updateApproximateCount(@PathVariable UUID id,
                                                                   @RequestParam BigDecimal count) {
        return ResponseEntity.ok(service.updateApproximateCount(id, count));
    }
}
