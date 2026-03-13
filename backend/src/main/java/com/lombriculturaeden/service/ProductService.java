package com.lombriculturaeden.service;

import com.lombriculturaeden.dto.ProductRequest;
import com.lombriculturaeden.dto.ProductResponse;
import com.lombriculturaeden.entity.Product;
import com.lombriculturaeden.exception.ResourceNotFoundException;
import com.lombriculturaeden.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private static final BigDecimal DEFAULT_STOCK = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_VOLUME = BigDecimal.ONE;
    
    private final ProductRepository repository;
    
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return repository.findAllByOrderByNameAsc().stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public ProductResponse findById(UUID id) {
        return repository.findById(id)
                .map(ProductResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> findLowStock() {
        return repository.findLowStock().stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
    
    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (repository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product already exists: " + request.getName());
        }
        
        Product entity = Product.builder()
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .unitOfMeasure(request.getUnitOfMeasure())
                .currentStock(request.getCurrentStock() != null ? request.getCurrentStock() : DEFAULT_STOCK)
                .minimumStock(request.getMinimumStock() != null ? request.getMinimumStock() : DEFAULT_STOCK)
                .unitPrice(request.getUnitPrice() != null ? request.getUnitPrice() : DEFAULT_PRICE)
                .unitCost(request.getUnitCost() != null ? request.getUnitCost() : DEFAULT_PRICE)
                .volumePerUnit(request.getVolumePerUnit() != null ? request.getVolumePerUnit() : DEFAULT_VOLUME)
                .build();
        
        return ProductResponse.fromEntity(repository.save(entity));
    }
    
    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        
        if (!entity.getName().equals(request.getName()) && repository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product already exists: " + request.getName());
        }
        
        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setDescription(request.getDescription());
        entity.setUnitOfMeasure(request.getUnitOfMeasure());
        if (request.getCurrentStock() != null) {
            entity.setCurrentStock(request.getCurrentStock());
        }
        if (request.getMinimumStock() != null) {
            entity.setMinimumStock(request.getMinimumStock());
        }
        if (request.getUnitPrice() != null) {
            entity.setUnitPrice(request.getUnitPrice());
        }
        if (request.getUnitCost() != null) {
            entity.setUnitCost(request.getUnitCost());
        }
        if (request.getVolumePerUnit() != null) {
            entity.setVolumePerUnit(request.getVolumePerUnit());
        }
        
        return ProductResponse.fromEntity(repository.save(entity));
    }
    
    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        repository.deleteById(id);
    }
}
