package org.example.shelftoshelf.Service;


import org.example.shelftoshelf.Entity.OtpVerification;
import org.example.shelftoshelf.Repo.OtpVerificationRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final OtpVerificationRepo otpVerificationRepo;
    private final EmailService emailService;

    public OtpService(OtpVerificationRepo otpVerificationRepo, EmailService emailService) {
        this.otpVerificationRepo = otpVerificationRepo;
        this.emailService = emailService;
    }

    public void sendOtp(String email) {

        String otp = String.format("%06d", new Random().nextInt(1000000));

        OtpVerification verification = new OtpVerification();

        verification.setEmail(email);
        verification.setOtp(otp);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        otpVerificationRepo.save(verification);
        emailService.sendOtp(email, otp);
    }

    public boolean verifyOtp(String email, String code) {

        OtpVerification otp = otpVerificationRepo
                .findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP tapılmadı"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP-nin müddəti bitib");
        }

        if (!otp.getOtp().equals(code)) {
            return false;
        }

        otpVerificationRepo.deleteById(otp.getId());
        return true;
    }
}
