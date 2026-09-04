package org.example.shelftoshelf.Service;

import org.example.shelftoshelf.Dto.Mapper.BookMaper;
import org.example.shelftoshelf.Dto.Mapper.CustomerMapper;
import org.example.shelftoshelf.Dto.Request.CustomerRequestDto;
import org.example.shelftoshelf.Dto.Response.CustomerProfileResponseDto;
import org.example.shelftoshelf.Dto.Response.CustomerPublicResponseDto;
import org.example.shelftoshelf.Entity.Book;
import org.example.shelftoshelf.Entity.Customer;
import org.example.shelftoshelf.Entity.Enum.BookStatus;
import org.example.shelftoshelf.Entity.Enum.CustomerRole;
import org.example.shelftoshelf.ExceptionManager.CustomerNotFoundException;
import org.example.shelftoshelf.Repo.BookRepo;
import org.example.shelftoshelf.Repo.CustomerRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private  final CustomerMapper customerMapper;
    private final CustomerRepo customerRepo;
    private  final BookRepo bookRepo;
    private  final BookMaper bookMaper;
    private final OtpService otpService;
    public CustomerService(CustomerMapper customerMapper, CustomerRepo customerRepo,BookRepo bookRepo,BookMaper bookMaper,OtpService otpService) {
        this.customerMapper = customerMapper;
        this.customerRepo = customerRepo;
        this.bookRepo=bookRepo;
        this.bookMaper=bookMaper;
        this.otpService=otpService;
    }

//user ucun
    public CustomerProfileResponseDto createAccount(CustomerRequestDto requestDto){
        Customer customer=customerMapper.toEntity(requestDto);
        customer.setRole(CustomerRole.USER);
        customer.setVerified(false);
        Customer savedCustomer=customerRepo.save(customer);
        otpService.sendOtp(savedCustomer.getEmail());
        return  customerMapper.toCustomerProfileResponseDto(savedCustomer);
    }


    public CustomerProfileResponseDto verifyOtp(
            String email,
            String otp) {

        boolean correct = otpService.verifyOtp(email, otp);

        if (!correct) {
            throw new RuntimeException("OTP kodu yanlışdır");
        }

        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "İstifadəçi tapılmadı"));

        customer.setVerified(true);

        Customer savedCustomer = customerRepo.save(customer);

        return customerMapper.toCustomerProfileResponseDto(savedCustomer);
    }



//herenin oz profili
    public CustomerProfileResponseDto getCustomerProfile(Long customerId) {
        Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı"));

        List<Book> books =bookRepo.findBySellerIdAndStatus(customerId, BookStatus.ACTIVE);
        CustomerProfileResponseDto dto = customerMapper.toCustomerProfileResponseDto(customer);
        dto.setBooks(books.stream().map(book ->  bookMaper.toResponseDto(book)).toList());
        return dto;
    }

    // 1 userin digerinde gordiyu
    public CustomerPublicResponseDto getPublicProfile(Long customerId) {

        Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı"));

        List<Book> books = bookRepo.findBySellerIdAndStatus(customerId, BookStatus.ACTIVE);
        CustomerPublicResponseDto dto = customerMapper.toCustomerPublicResponseDto(customer);
        dto.setBooks(books.stream()
                        .map(bookMaper::toResponseDto)
                        .toList()
        );

        return dto;
    }
    // user məlumatlarını yeniləmək
    public CustomerProfileResponseDto updateCustomer(
            Long customerId,
            CustomerRequestDto requestDto) {

        Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı"));
        customer.setName(requestDto.getName());
        customer.setSurname(requestDto.getSurname());
        customer.setEmail(requestDto.getEmail());
        customer.setPhone(requestDto.getPhone());
        customer.setPassword(requestDto.getPassword());
        Customer updatedCustomer = customerRepo.save(customer);
        return customerMapper.toCustomerProfileResponseDto(updatedCustomer);
    }


    // user silmək yalniz admin  ede bolor
    public void deleteCustomer(Long customerId) {
        Customer customer = customerRepo.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı"));
        customerRepo.delete(customer);
    }














}
