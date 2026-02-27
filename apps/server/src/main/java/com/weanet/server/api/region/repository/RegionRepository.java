package com.weanet.server.api.region.repository;

import com.weanet.server.api.region.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByLocationName(String name);
}
