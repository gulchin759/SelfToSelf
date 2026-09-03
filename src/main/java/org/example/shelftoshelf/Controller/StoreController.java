package org.example.shelftoshelf.Controller;

import org.example.shelftoshelf.Dto.Request.StoreRequestDto;
import org.example.shelftoshelf.Dto.Response.StoreProfileResponceDto;
import org.example.shelftoshelf.Dto.Response.StorePublicResponceDto;
import org.example.shelftoshelf.Service.StoreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }
//admin
    @PostMapping
    public StoreProfileResponceDto createStore(
            @RequestBody StoreRequestDto requestDto) {

        return storeService.createStore(requestDto);
    }
//her magaza oz prolini gorur
    @GetMapping("/{id}/profile")
    public StoreProfileResponceDto getMyProfile(
            @PathVariable Long id) {

        return storeService.getMyProfile(id);
    }

    //istifadecinin gorduyu magaza haqiqinda
    @GetMapping("/{id}")
    public StorePublicResponceDto getPublic(
            @PathVariable Long id) {

        return storeService.getPublic(id);
    }

//magaza update etemk admin edir
    @PutMapping("/{id}")
    public StoreProfileResponceDto updateStore(
            @PathVariable Long id,
            @RequestBody StoreRequestDto requestDto) {

        return storeService.updateStore(id, requestDto);
    }

    // Mağazanı sil admin edir
    @DeleteMapping("/{id}")
    public void deleteStore(
            @PathVariable Long id) {

        storeService.deleteStore(id);
    }

//hem admin hemde istifadeci
    @GetMapping("/search")
    public List<StorePublicResponceDto> searchByName(
            @RequestParam String name) {

        return storeService.searchByName(name);
    }

//admin istifadeci
    @GetMapping
    public List<StoreProfileResponceDto> getAllStores() {

        return storeService.getAllStores();
    }
}
