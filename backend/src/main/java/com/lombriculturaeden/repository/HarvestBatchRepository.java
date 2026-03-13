package com.lombriculturaeden.repository;

import com.lombriculturaeden.entity.HarvestBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HarvestBatchRepository extends JpaRepository<HarvestBatch, UUID> {
    List<HarvestBatch> findAllByOrderByHarvestDateDesc();
}
