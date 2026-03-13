package com.lombriculturaeden.service;

import com.lombriculturaeden.dto.DashboardResponse;
import com.lombriculturaeden.dto.ProductResponse;
import com.lombriculturaeden.entity.HarvestBatch;
import com.lombriculturaeden.entity.HumusProduction;
import com.lombriculturaeden.entity.Product;
import com.lombriculturaeden.entity.Sale;
import com.lombriculturaeden.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final HarvestBatchRepository harvestBatchRepository;
    private final HumusProductionRepository humusProductionRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        List<HarvestBatch> batches = harvestBatchRepository.findAll();
        List<HumusProduction> productions = humusProductionRepository.findAll();
        List<Product> products = productRepository.findAll();
        List<Product> lowStockProducts = productRepository.findLowStock();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfYear = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        
        BigDecimal totalRevenue = saleRepository.sumTotalByStatusAndDateBetween(Sale.SaleStatus.PAID, startOfYear, now);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        
        List<DashboardResponse.MonthlyProduction> monthlyProductions = getMonthlyProductions(productions);
        List<DashboardResponse.MonthlySales> monthlySales = getMonthlySales(now);
        
        List<ProductResponse> lowStockAlerts = lowStockProducts.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        
        return DashboardResponse.builder()
                .totalHarvestBatches(BigDecimal.valueOf(batches.size()))
                .totalActiveBatches(BigDecimal.valueOf(batches.size()))
                .totalHumusProduction(BigDecimal.valueOf(productions.size()))
                .totalLitersProduced(productions.stream()
                        .map(HumusProduction::getQuantityLiters)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .totalSales(BigDecimal.valueOf(saleRepository.count()))
                .totalRevenue(totalRevenue)
                .totalProducts(BigDecimal.valueOf(products.size()))
                .lowStockProducts(BigDecimal.valueOf(lowStockProducts.size()))
                .monthlyProductions(monthlyProductions)
                .monthlySales(monthlySales)
                .lowStockAlerts(lowStockAlerts)
                .build();
    }
    
    private List<DashboardResponse.MonthlyProduction> getMonthlyProductions(List<HumusProduction> productions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
        Map<String, BigDecimal> monthlyMap = new HashMap<>();
        
        for (HumusProduction p : productions) {
            String month = p.getProductionDate().format(formatter);
            monthlyMap.merge(month, p.getQuantityLiters(), BigDecimal::add);
        }
        
        return monthlyMap.entrySet().stream()
                .map(e -> DashboardResponse.MonthlyProduction.builder()
                        .month(e.getKey())
                        .quantity(e.getValue())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<DashboardResponse.MonthlySales> getMonthlySales(LocalDateTime now) {
        LocalDateTime startOfYear = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        List<Sale> sales = saleRepository.findBySaleDateBetween(startOfYear, now);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
        Map<String, BigDecimal> monthlyMap = new HashMap<>();
        
        for (Sale s : sales) {
            String month = s.getSaleDate().format(formatter);
            monthlyMap.merge(month, s.getTotal(), BigDecimal::add);
        }
        
        return monthlyMap.entrySet().stream()
                .map(e -> DashboardResponse.MonthlySales.builder()
                        .month(e.getKey())
                        .total(e.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}
