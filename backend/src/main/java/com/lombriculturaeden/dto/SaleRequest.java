package com.lombriculturaeden.dto;

import com.lombriculturaeden.entity.Sale;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequest {
    
    private UUID customerId;
    
    @NotNull(message = "Sale date is required")
    private LocalDateTime saleDate;
    
    private Sale.SaleStatus status;
    
    private String observations;
    
    @NotNull(message = "Sale details are required")
    private List<SaleDetailRequest> details;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleDetailRequest {
        @NotNull(message = "Product is required")
        private UUID productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        private BigDecimal quantity;
        
        @NotNull(message = "Unit price is required")
        @Positive(message = "Unit price must be greater than 0")
        private BigDecimal unitPrice;
    }
}
