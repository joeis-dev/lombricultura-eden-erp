package com.lombriculturaeden.controller;

import com.lombriculturaeden.dto.InventoryMovementRequest;
import com.lombriculturaeden.dto.InventoryMovementResponse;
import com.lombriculturaeden.service.InventoryMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory-movements")
@RequiredArgsConstructor
@Tag(name = "Inventory Movements", description = "Inventory movement tracking API")
public class InventoryMovementController {
    
    private final InventoryMovementService service;
    
    @GetMapping
    @Operation(summary = "Get all inventory movements")
    public ResponseEntity<List<InventoryMovementResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory movements by product")
    public ResponseEntity<Page<InventoryMovementResponse>> findByProduct(@PathVariable UUID productId, 
                                                                          Pageable pageable) {
        return ResponseEntity.ok(service.findByProduct(productId, pageable));
    }
    
    @PostMapping
    @Operation(summary = "Create a new inventory movement")
    public ResponseEntity<InventoryMovementResponse> create(@Valid @RequestBody InventoryMovementRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }
}
