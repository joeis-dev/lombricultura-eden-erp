package com.lombriculturaeden.repository;

import com.lombriculturaeden.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {
    List<Sale> findAllByOrderBySaleDateDesc();
    
    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(s.total) FROM Sale s WHERE s.status = :status AND s.saleDate BETWEEN :start AND :end")
    BigDecimal sumTotalByStatusAndDateBetween(Sale.SaleStatus status, LocalDateTime start, LocalDateTime end);
}
