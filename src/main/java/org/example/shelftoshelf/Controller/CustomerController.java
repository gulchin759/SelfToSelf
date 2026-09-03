package org.example.shelftoshelf.Controller;

import org.example.shelftoshelf.Dto.Request.CustomerRequestDto;
import org.example.shelftoshelf.Dto.Response.CustomerProfileResponseDto;
import org.example.shelftoshelf.Dto.Response.CustomerPublicResponseDto;
import org.example.shelftoshelf.Service.CustomerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    // User qeydiyyatdan keçir
    @PostMapping
    public CustomerProfileResponseDto createAccount(
            @RequestBody CustomerRequestDto requestDto) {

        return customerService.createAccount(requestDto);
    }

    // Öz profilini görür
    @GetMapping("/{id}/profile")
    public CustomerProfileResponseDto getCustomerProfile(
            @PathVariable Long id) {

        return customerService.getCustomerProfile(id);
    }

    // Başqa userin gördüyü public profil
    @GetMapping("/{id}")
    public CustomerPublicResponseDto getPublicProfile(
            @PathVariable Long id) {

        return customerService.getPublicProfile(id);
    }

    // User məlumatlarını yeniləyir
    @PutMapping("/{id}")
    public CustomerProfileResponseDto updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDto requestDto) {

        return customerService.updateCustomer(id, requestDto);
    }

    // Useri silir - sonradan yalnız ADMIN üçün Security ilə məhdudlaşdırılacaq
    @DeleteMapping("/{id}")
    public void deleteCustomer(
            @PathVariable Long id) {

        customerService.deleteCustomer(id);
    }









}
