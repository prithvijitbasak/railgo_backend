package com.railgo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.railgo.entity.UserEntity;

import java.util.UUID;

public record UserDetailsDto(
        @JsonProperty("user_id") Long userId,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("email") String email,
        @JsonProperty("phone") String phone
) {

    public static UserDetailsDto from(UserEntity user) {
        return new UserDetailsDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }
}