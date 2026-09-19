package com.railgo.repository;

import com.railgo.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, UUID> {
    // Fetches the most recently created OTP for this email
    Optional<OtpEntity> findTopByEmailOrderByCreatDesc(String email);
}
