package com.daengddang.daengdong_map.repository;

import com.daengddang.daengdong_map.domain.dog.Dog;
import com.daengddang.daengdong_map.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Long> {

    Optional<Dog> findByUser(User user);

}
