package com.lombriculturaeden.repository;

import com.lombriculturaeden.entity.WormBed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WormBedRepository extends JpaRepository<WormBed, UUID> {
    List<WormBed> findAllByOrderByNameAsc();
    Optional<WormBed> findByName(String name);
    boolean existsByName(String name);
}
