package com.daengddang.daengdong_map.repository;

import com.daengddang.daengdong_map.domain.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Refresh Token Repository
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 토큰 값으로 RefreshToken 조회
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자 ID로 모든 RefreshToken 삭제 (로그아웃 시 사용)
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    /**
     * 만료된 토큰 일괄 삭제 (스케줄러에서 사용)
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    int deleteAllExpiredBefore(@Param("now") LocalDateTime now);

    /**
     * 사용자의 토큰 존재 여부 확인
     */
    boolean existsByUserId(Long userId);
}
