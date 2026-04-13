package com.lombriculturaeden.repository;

import com.lombriculturaeden.entity.WormBedModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WormBedModificationRepository extends JpaRepository<WormBedModification, UUID> {
    List<WormBedModification> findByWormBedIdOrderByModificationDateDesc(UUID wormBedId);
    List<WormBedModification> findAllByOrderByModificationDateDesc();
}
