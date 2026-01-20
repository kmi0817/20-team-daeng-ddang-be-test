package com.daengddang.daengdong_map.repository;

import com.daengddang.daengdong_map.domain.region.Region;
import com.daengddang.daengdong_map.domain.region.RegionStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByParentIsNullAndStatusOrderByNameAsc(RegionStatus status);
    List<Region> findByParentAndStatusOrderByNameAsc(Region parent, RegionStatus status);
    Optional<Region> findByIdAndStatus(Long id, RegionStatus status);
}
