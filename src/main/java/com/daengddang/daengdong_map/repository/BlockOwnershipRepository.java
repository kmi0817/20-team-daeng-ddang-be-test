package com.daengddang.daengdong_map.repository;

import com.daengddang.daengdong_map.domain.block.BlockOwnership;
import com.daengddang.daengdong_map.domain.dog.Dog;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlockOwnershipRepository extends JpaRepository<BlockOwnership, Long> {

    List<BlockOwnership> findAllByDog(Dog dog);

    @Query("""
            select ownership
            from BlockOwnership ownership
            join fetch ownership.block block
            join fetch ownership.dog dog
            where ownership.dog = :dog
            """)
    List<BlockOwnership> findAllByDogWithBlockAndDog(@Param("dog") Dog dog);

    List<BlockOwnership> findAllByIdIn(Collection<Long> blockIds);

    @Query("""
            select ownership
            from BlockOwnership ownership
            join fetch ownership.block block
            join fetch ownership.dog dog
            where block.x between :minX and :maxX
              and block.y between :minY and :maxY
            """)
    List<BlockOwnership> findAllByBlockRange(
            @Param("minX") int minX,
            @Param("maxX") int maxX,
            @Param("minY") int minY,
            @Param("maxY") int maxY
    );

    @Modifying
    @Query(value = """
            UPDATE block_ownership
            SET dog_id = :dogId,
                acquired_at = :updatedAt,
                last_passed_at = :updatedAt,
                updated_at = :updatedAt
            WHERE block_id = :blockId
            """, nativeQuery = true)
    void restoreOwner(
            @Param("blockId") Long blockId,
            @Param("dogId") Long dogId,
            @Param("updatedAt") java.time.LocalDateTime updatedAt
    );
}
