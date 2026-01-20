package com.daengddang.daengdong_map.dto.response.dog;

import com.daengddang.daengdong_map.domain.dog.Dog;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DogResponse {

    private final Long dogId;
    private final String name;
    private final String breed;
    private final LocalDate birthDate;
    private final Float weight;
    private final String gender;
    private final boolean isNeutered;
    private final String profileImageUrl;

    @Builder
    private DogResponse(Long dogId,
                        String name,
                        String breed,
                        LocalDate birthDate,
                        Float weight,
                        String gender,
                        boolean isNeutered,
                        String profileImageUrl) {
        this.dogId = dogId;
        this.name = name;
        this.breed = breed;
        this.birthDate = birthDate;
        this.weight = weight;
        this.gender = gender;
        this.isNeutered = isNeutered;
        this.profileImageUrl = profileImageUrl;
    }

    public static DogResponse from(Dog dog) {
        String genderDisplayName = dog.getGender() == null ? null : dog.getGender().getDisplayName();
        return DogResponse.builder()
                .dogId(dog.getId())
                .name(dog.getName())
                .breed(dog.getBreed().getName())
                .birthDate(dog.getBirthDate())
                .weight(dog.getWeight())
                .gender(genderDisplayName)
                .isNeutered(dog.isNeutered())
                .profileImageUrl(dog.getProfileImageUrl())
                .build();
    }
}
