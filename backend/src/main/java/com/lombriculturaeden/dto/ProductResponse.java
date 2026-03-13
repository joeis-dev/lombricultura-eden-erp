package com.lombriculturaeden.dto;

import com.lombriculturaeden.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String name;
    private Product.ProductType type;
    private String description;
    private String unitOfMeasure;
    private BigDecimal currentStock;
    private BigDecimal minimumStock;
    private BigDecimal unitPrice;
    private BigDecimal unitCost;
    private boolean lowStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ProductResponse fromEntity(Product entity) {
        boolean lowStock = entity.getCurrentStock().compareTo(entity.getMinimumStock()) <= 0;
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .description(entity.getDescription())
                .unitOfMeasure(entity.getUnitOfMeasure())
                .currentStock(entity.getCurrentStock())
                .minimumStock(entity.getMinimumStock())
                .unitPrice(entity.getUnitPrice())
                .unitCost(entity.getUnitCost())
                .lowStock(lowStock)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
