package com.lombriculturaeden.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal totalHarvestBatches;
    private BigDecimal totalActiveBatches;
    private BigDecimal totalHumusProduction;
    private BigDecimal totalLitersProduced;
    private BigDecimal totalSales;
    private BigDecimal totalRevenue;
    private BigDecimal totalProducts;
    private BigDecimal lowStockProducts;
    private List<MonthlyProduction> monthlyProductions;
    private List<MonthlySales> monthlySales;
    private List<ProductResponse> lowStockAlerts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyProduction {
        private String month;
        private BigDecimal quantity;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlySales {
        private String month;
        private BigDecimal total;
    }
}
