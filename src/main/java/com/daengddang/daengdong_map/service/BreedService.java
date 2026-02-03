package com.daengddang.daengdong_map.service;

import com.daengddang.daengdong_map.common.ErrorCode;
import com.daengddang.daengdong_map.common.exception.BaseException;
import com.daengddang.daengdong_map.domain.breed.Breed;
import com.daengddang.daengdong_map.dto.response.dog.BreedListResponse;
import com.daengddang.daengdong_map.repository.BreedRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BreedService {

    private final BreedRepository breedRepository;

    public BreedListResponse getBreeds(String keyword) {
        List<Breed> breeds;
        if (keyword == null) {
            breeds = breedRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        } else {
            String trimmed = keyword.trim();
            if (trimmed.isEmpty()) {
                throw new BaseException(ErrorCode.SEARCH_KEYWORD_TOO_SHORT);
            }
            breeds = breedRepository.findByNameContainingOrderByNameAsc(trimmed);
        }
        return BreedListResponse.from(breeds);
    }
}
