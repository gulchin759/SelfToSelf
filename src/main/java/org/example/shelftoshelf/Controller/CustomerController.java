package org.example.shelftoshelf.Controller;

import org.example.shelftoshelf.Dto.Request.CustomerRequestDto;
import org.example.shelftoshelf.Dto.Response.BookResponseDto;
import org.example.shelftoshelf.Dto.Response.CustomerProfileResponseDto;
import org.example.shelftoshelf.Dto.Response.CustomerPublicResponseDto;
import org.example.shelftoshelf.Service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // İstifadəçi qeydiyyatı
    @PostMapping
    public CustomerProfileResponseDto createAccount(
            @RequestBody CustomerRequestDto requestDto) {
        return customerService.createAccount(requestDto);
    }

    // OTP təsdiqləmə
    @PostMapping("/verify-otp")
    public CustomerProfileResponseDto verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {
        return customerService.verifyOtp(email, otp);
    }

    // İstifadəçinin öz profil məlumatları
    @GetMapping("/me")
    public ResponseEntity<CustomerProfileResponseDto> getMyProfile() {
        return ResponseEntity.ok(customerService.getMyProfile());
    }

    // İstifadəçinin öz məlumatlarını yeniləməsi
    @PutMapping("/me")
    public ResponseEntity<CustomerProfileResponseDto> updateMyProfile(
            @RequestBody CustomerRequestDto requestDto) {
        return ResponseEntity.ok(customerService.updateMyProfile(requestDto));
    }

    // Wishlist (Sevimlilər) əlavə et / çıxar
    @PostMapping("/wishlist/{bookId}")
    public ResponseEntity<Map<String, Object>> toggleWishlist(@PathVariable Long bookId) {
        boolean added = customerService.toggleWishlist(bookId);
        return ResponseEntity.ok(Map.of("bookId", bookId, "added", added));
    }

    // Sevimlilər siyahısı
    @GetMapping("/wishlist")
    public ResponseEntity<List<BookResponseDto>> getWishlist() {
        return ResponseEntity.ok(customerService.getWishlist());
    }

    // Bütün istifadəçilərin siyahısı (Yalnız ADMIN üçün - kitab sayları ilə)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerProfileResponseDto>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // Öz profilini görür (ID üzrə)
    @GetMapping("/{id}/profile")
    public CustomerProfileResponseDto getCustomerProfile(
            @PathVariable Long id) {
        return customerService.getCustomerProfile(id);
    }

    // Başqa userin gördüyü public profil və kitabları
    @GetMapping("/{id}")
    public CustomerPublicResponseDto getPublicProfile(
            @PathVariable Long id) {
        return customerService.getPublicProfile(id);
    }

    // User məlumatlarını yeniləyir (ID üzrə)
    @PutMapping("/{id}")
    public CustomerProfileResponseDto updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDto requestDto) {
        return customerService.updateCustomer(id, requestDto);
    }

    // Useri silir (Yalnız ADMIN üçün)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
