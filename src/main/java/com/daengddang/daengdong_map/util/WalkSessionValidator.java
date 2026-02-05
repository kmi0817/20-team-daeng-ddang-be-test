package com.daengddang.daengdong_map.util;

import com.daengddang.daengdong_map.domain.walk.Walk;
import com.daengddang.daengdong_map.domain.walk.WalkStatus;
import com.daengddang.daengdong_map.repository.WalkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalkSessionValidator {

    private final WalkRepository walkRepository;

    public Walk getActiveWalkOrNull(Long walkId) {
        Walk walk = walkRepository.findById(walkId).orElse(null);
        if (walk == null || walk.getStatus() != WalkStatus.IN_PROGRESS) {
            return null;
        }
        return walk;
    }

    public boolean isValidCoordinate(double lat, double lng) {
        return CoordinateValidator.isValidLatLng(lat, lng);
    }
}
