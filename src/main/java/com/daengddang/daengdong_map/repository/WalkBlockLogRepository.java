package com.daengddang.daengdong_map.repository;

import com.daengddang.daengdong_map.domain.walk.WalkBlockLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalkBlockLogRepository extends JpaRepository<WalkBlockLog, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO walk_block_logs (walk_id, block_id, dog_id, acquired_at)
            VALUES (:walkId, :blockId, :dogId, :acquiredAt)
            ON CONFLICT (walk_id, block_id) DO NOTHING
            """, nativeQuery = true)
    void insertIfNotExists(
            @Param("walkId") Long walkId,
            @Param("blockId") Long blockId,
            @Param("dogId") Long dogId,
            @Param("acquiredAt") java.time.LocalDateTime acquiredAt
    );

    @Query("""
            select log.block.id
            from WalkBlockLog log
            where log.walk.id = :walkId
            """)
    List<Long> findBlockIdsByWalkId(@Param("walkId") Long walkId);
}
