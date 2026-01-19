package com.daengddang.daengdong_map.repository;

import com.daengddang.daengdong_map.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoUserId(Long kakaoUserId);

    boolean existsByKakaoUserId(Long kakaoUserId);
}