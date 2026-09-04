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

    public void sendOtp(String email) {String otp = String.format("%06d",new Random().nextInt(1000000));

        OtpVerification verification = otpVerificationRepo.findByEmail(email).orElse(new OtpVerification());
        verification.setEmail(email);
        verification.setOtp(otp);
        verification.setExpiresAt(
                LocalDateTime.now().plusMinutes(5)
        );

        otpVerificationRepo.save(verification);
        emailService.sendOtp(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {

        OtpVerification verification =
                otpVerificationRepo.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("OTP tapılmadı"));

        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException("OTP-nin vaxtı bitib");
        }

        if (!verification.getOtp().equals(otp)) {

            return false;
        }

        otpVerificationRepo.delete(verification);

        return true;
    }
}
