package com.daengddang.daengdong_map.repository;

import com.daengddang.daengdong_map.domain.diary.WalkDiary;
import com.daengddang.daengdong_map.repository.projection.DailyRecordView;
import com.daengddang.daengdong_map.repository.projection.DateCountView;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalkDiaryRepository extends JpaRepository<WalkDiary, Long>  {

    @Query(value = """
            SELECT CAST(wd.created_at AS DATE) AS date, COUNT(*) AS count
            FROM walk_diaries wd
            WHERE wd.user_id = :userId
              AND wd.created_at >= :startAt
              AND wd.created_at < :endAt
            GROUP BY CAST(wd.created_at AS DATE)
            """, nativeQuery = true)
    List<DateCountView> countWalkDiaryByDate(
            @Param("userId") Long userId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    @Query(value = """
            SELECT wd.walk_diary_id AS id, wd.created_at AS createdAt
            FROM walk_diaries wd
            WHERE wd.user_id = :userId
              AND wd.created_at >= :startAt
              AND wd.created_at < :endAt
            ORDER BY wd.created_at ASC
            """, nativeQuery = true)
    List<DailyRecordView> findDailyWalkRecords(
            @Param("userId") Long userId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );
}
