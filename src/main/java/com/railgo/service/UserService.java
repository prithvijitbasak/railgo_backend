package com.railgo.service;

import com.railgo.dto.UserDetailsDto;
import com.railgo.entity.UserEntity;
import com.railgo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserDetailsDto> getUserDetails(Long userId) {
        return userRepository.findById(userId).map(UserDetailsDto::from);
    }

    public Optional<UserDetailsDto> getUserDetailsIfSessionValid(Long userId, String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findById(userId)
                .filter(user -> sessionId.equals(user.getCurrentSessionId()))
                .map(UserDetailsDto::from);
    }

    public Optional<UserEntity> findById(Long userId) {
        return userRepository.findById(userId);
    }
}
