package org.example.shelftoshelf.Controller;

import org.example.shelftoshelf.Dto.Request.AuthRequestDto;
import org.example.shelftoshelf.Dto.Request.CustomerRequestDto;
import org.example.shelftoshelf.Dto.Request.OtpVerifyRequestDto;
import org.example.shelftoshelf.Dto.Response.AuthResponseDto;
import org.example.shelftoshelf.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // İstifadəçi qeydiyyatı (OTP göndərilir)
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody CustomerRequestDto requestDto) {
        return ResponseEntity.ok(authService.register(requestDto));
    }

    // OTP Təsdiqləmə (Hesab aktivləşdirilir və JWT qaytarılır)
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponseDto> verifyOtp(@RequestBody OtpVerifyRequestDto requestDto) {
        return ResponseEntity.ok(authService.verifyOtp(requestDto));
    }

    // OTP-ni yenidən göndərmək
    @PostMapping("/resend-otp")
    public ResponseEntity<Map<String, String>> resendOtp(@RequestParam String email) {
        authService.resendOtp(email);
        return ResponseEntity.ok(Map.of("message", "Yeni OTP kodu email ünvanınıza göndərildi."));
    }

    // Ümumi giriş
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }

    // Yalnız USER (İstifadəçi) üçün ayrıca giriş
    @PostMapping("/user/login")
    public ResponseEntity<AuthResponseDto> userLogin(@RequestBody AuthRequestDto requestDto) {
        return ResponseEntity.ok(authService.userLogin(requestDto));
    }

    // Yalnız STORE (Mağaza) üçün ayrıca giriş
    @PostMapping("/store/login")
    public ResponseEntity<AuthResponseDto> storeLogin(@RequestBody AuthRequestDto requestDto) {
        return ResponseEntity.ok(authService.storeLogin(requestDto));
    }

    // Yalnız ADMIN üçün ayrıca giriş
    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponseDto> adminLogin(@RequestBody AuthRequestDto requestDto) {
        return ResponseEntity.ok(authService.adminLogin(requestDto));
    }

    // Hal-hazırda daxil olmuş istifadəçi məlumatları
    @GetMapping("/me")
    public ResponseEntity<AuthResponseDto> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
