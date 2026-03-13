package com.lombriculturaeden.controller;

import com.lombriculturaeden.dto.SaleRequest;
import com.lombriculturaeden.dto.SaleResponse;
import com.lombriculturaeden.entity.Sale;
import com.lombriculturaeden.service.SaleService;
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
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sales management API")
public class SaleController {
    
    private final SaleService service;
    
    @GetMapping
    @Operation(summary = "Get all sales")
    public ResponseEntity<List<SaleResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get sale by ID")
    public ResponseEntity<SaleResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @PostMapping
    @Operation(summary = "Create a new sale")
    public ResponseEntity<SaleResponse> create(@Valid @RequestBody SaleRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update sale status")
    public ResponseEntity<SaleResponse> updateStatus(@PathVariable UUID id, 
                                                       @RequestParam Sale.SaleStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }
}
