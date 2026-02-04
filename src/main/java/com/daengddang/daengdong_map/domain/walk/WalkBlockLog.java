package com.daengddang.daengdong_map.domain.walk;

import com.daengddang.daengdong_map.domain.block.Block;
import com.daengddang.daengdong_map.domain.dog.Dog;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "walk_block_logs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_walk_block_logs_walk_block", columnNames = {"walk_id", "block_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "walk_block_log_seq_generator",
        sequenceName = "walk_block_logs_walk_block_log_id_seq",
        allocationSize = 1
)
public class WalkBlockLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "walk_block_log_seq_generator")
    @Column(name = "walk_block_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walk_id", nullable = false)
    private Walk walk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    @Column(name = "acquired_at", nullable = false)
    private LocalDateTime acquiredAt;

    @Builder
    private WalkBlockLog(Walk walk, Block block, Dog dog, LocalDateTime acquiredAt) {
        this.walk = walk;
        this.block = block;
        this.dog = dog;
        this.acquiredAt = acquiredAt;
    }

    @PrePersist
    private void onCreate() {
        if (acquiredAt == null) {
            acquiredAt = LocalDateTime.now();
        }
    }
}
