package com.lombriculturaeden.repository;

import com.lombriculturaeden.entity.HumusProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HumusProductionRepository extends JpaRepository<HumusProduction, UUID> {
    List<HumusProduction> findByHarvestBatchId(UUID batchId);
    List<HumusProduction> findAllByOrderByProductionDateDesc();
}
