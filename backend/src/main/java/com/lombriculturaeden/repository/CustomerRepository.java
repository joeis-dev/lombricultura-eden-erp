package com.lombriculturaeden.repository;

import com.lombriculturaeden.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findAllByOrderByNameAsc();
    boolean existsByDocument(String document);
}
