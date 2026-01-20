package com.daengddang.daengdong_map.repository;

import com.daengddang.daengdong_map.domain.dog.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DogRepository extends JpaRepository<Dog, Long> {
}
