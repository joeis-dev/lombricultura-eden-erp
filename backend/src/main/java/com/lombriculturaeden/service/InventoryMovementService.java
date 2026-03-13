package com.lombriculturaeden.service;

import com.lombriculturaeden.dto.InventoryMovementRequest;
import com.lombriculturaeden.dto.InventoryMovementResponse;
import com.lombriculturaeden.entity.InventoryMovement;
import com.lombriculturaeden.entity.Product;
import com.lombriculturaeden.exception.ResourceNotFoundException;
import com.lombriculturaeden.repository.InventoryMovementRepository;
import com.lombriculturaeden.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryMovementService {
    
    private final InventoryMovementRepository movementRepository;
    private final ProductRepository productRepository;
    
    @Transactional(readOnly = true)
    public List<InventoryMovementResponse> findAll() {
        return movementRepository.findAll().stream()
                .map(InventoryMovementResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Page<InventoryMovementResponse> findByProduct(UUID productId, Pageable pageable) {
        return movementRepository.findByProductIdOrderByMovementDateDesc(productId, pageable)
                .map(InventoryMovementResponse::fromEntity);
    }
    
    @Transactional
    public InventoryMovementResponse create(InventoryMovementRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));
        
        InventoryMovement movement = InventoryMovement.builder()
                .product(product)
                .movementType(request.getMovementType())
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .movementDate(request.getMovementDate() != null ? request.getMovementDate() : LocalDateTime.now())
                .build();
        
        updateProductStock(product, request);
        
        return InventoryMovementResponse.fromEntity(movementRepository.save(movement));
    }
    
    private void updateProductStock(Product product, InventoryMovementRequest request) {
        switch (request.getMovementType()) {
            case IN -> product.setCurrentStock(product.getCurrentStock().add(request.getQuantity()));
            case OUT -> product.setCurrentStock(product.getCurrentStock().subtract(request.getQuantity()));
            case ADJUSTMENT -> product.setCurrentStock(request.getQuantity());
        }
        productRepository.save(product);
    }
}
