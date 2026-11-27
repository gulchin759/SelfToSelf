package org.example.shelftoshelf.Controller;

import org.example.shelftoshelf.Dto.Request.StoreRequestDto;
import org.example.shelftoshelf.Dto.Response.StoreProfileResponceDto;
import org.example.shelftoshelf.Dto.Response.StorePublicResponceDto;
import org.example.shelftoshelf.Service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    // Yalnız ADMIN yeni mağaza yarada bilər
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StoreProfileResponceDto> createStore(
            @RequestBody StoreRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.createStore(requestDto));
    }

    // Mağaza daxil olduqda öz profilinə və kitablarına baxır
    @GetMapping("/my-store")
    @PreAuthorize("hasRole('STORE')")
    public ResponseEntity<StoreProfileResponceDto> getMyStoreProfile() {
        return ResponseEntity.ok(storeService.getMyStoreProfile());
    }

    // Mağaza daxil olduqda öz məlumatlarını redaktə edir
    @PutMapping("/my-store")
    @PreAuthorize("hasRole('STORE')")
    public ResponseEntity<StoreProfileResponceDto> updateMyStore(
            @RequestBody StoreRequestDto requestDto) {
        return ResponseEntity.ok(storeService.updateMyStore(requestDto));
    }

    // İctimai mağaza məlumatı və kitabları (hər kəs detallara baxanda görə bilər)
    @GetMapping("/{id}")
    public ResponseEntity<StorePublicResponceDto> getPublic(
            @PathVariable Long id) {
        return ResponseEntity.ok(storeService.getPublic(id));
    }

    // Mağaza profilinə baxış (ID üzrə)
    @GetMapping("/{id}/profile")
    public ResponseEntity<StoreProfileResponceDto> getMyProfile(
            @PathVariable Long id) {
        return ResponseEntity.ok(storeService.getMyProfile(id));
    }

    // Mağazanı redaktə et (Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StoreProfileResponceDto> updateStore(
            @PathVariable Long id,
            @RequestBody StoreRequestDto requestDto) {
        return ResponseEntity.ok(storeService.updateStore(id, requestDto));
    }

    // Mağazanı sil (Yalnız ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStore(
            @PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    // Ad üzrə axtarış (İctimai)
    @GetMapping("/search")
    public List<StorePublicResponceDto> searchByName(
            @RequestParam String name) {
        return storeService.searchByName(name);
    }

    // Bütün mağazaların siyahısı (Ad, yerləşmə, kitab sayı)
    @GetMapping
    public List<StoreProfileResponceDto> getAllStores() {
        return storeService.getAllStores();
    }
}
