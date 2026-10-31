package com.railgo.service;

import com.railgo.dto.UserDetailsDto;
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
}