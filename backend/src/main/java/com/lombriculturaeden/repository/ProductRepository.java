package com.lombriculturaeden.repository;

import com.lombriculturaeden.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByOrderByNameAsc();
    
    @Query("SELECT p FROM Product p WHERE p.currentStock <= p.minimumStock")
    List<Product> findLowStock();
    
    boolean existsByName(String name);
}
