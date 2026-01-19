package com.daengddang.daengdong_map.repository;

import com.daengddang.daengdong_map.domain.region.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
