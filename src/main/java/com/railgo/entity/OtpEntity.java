package com.railgo.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="user_otps")
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "otp_id")
    private UUID otpId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 6)
    private String otp;

    @Column(nullable = false)
    private Instant creat;

    @Column(nullable = false)
    private Instant exp;

    @Column(nullable = false)
    private Short used = 0;

    public UUID getOtpId() {
        return otpId;
    }

    public void setOtpId(UUID otpId) {
        this.otpId = otpId;
    }

    // Convenience delegates for backwards compatibility
    public UUID getId() {
        return otpId;
    }

    public void setId(UUID id) {
        this.otpId = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Instant getCreat() {
        return creat;
    }

    public void setCreat(Instant creat) {
        this.creat = creat;
    }

    public Instant getExp() {
        return exp;
    }

    public void setExp(Instant exp) {
        this.exp = exp;
    }

    public Short getUsed() {
        return used;
    }

    public void setUsed(Short used) {
        this.used = used;
    }
}
