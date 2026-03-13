package com.lombriculturaeden.dto;

import com.lombriculturaeden.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Type is required")
    private Product.ProductType type;
    
    private String description;
    
    @NotBlank(message = "Unit of measure is required")
    private String unitOfMeasure;
    
    @PositiveOrZero(message = "Current stock must be zero or positive")
    private BigDecimal currentStock;
    
    @PositiveOrZero(message = "Minimum stock must be zero or positive")
    private BigDecimal minimumStock;
    
    @PositiveOrZero(message = "Unit price must be zero or positive")
    private BigDecimal unitPrice;
    
    @PositiveOrZero(message = "Unit cost must be zero or positive")
    private BigDecimal unitCost;
    
    @Positive(message = "Volume per unit must be greater than zero")
    @Builder.Default
    private BigDecimal volumePerUnit = BigDecimal.ONE;
}
