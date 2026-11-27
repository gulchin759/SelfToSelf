package org.example.shelftoshelf.Service;

import org.example.shelftoshelf.Dto.Mapper.BookMaper;
import org.example.shelftoshelf.Dto.Mapper.CustomerMapper;
import org.example.shelftoshelf.Dto.Request.CustomerRequestDto;
import org.example.shelftoshelf.Dto.Response.BookResponseDto;
import org.example.shelftoshelf.Dto.Response.CustomerProfileResponseDto;
import org.example.shelftoshelf.Dto.Response.CustomerPublicResponseDto;
import org.example.shelftoshelf.Entity.Book;
import org.example.shelftoshelf.Entity.Customer;
import org.example.shelftoshelf.Entity.Enum.BookStatus;
import org.example.shelftoshelf.Entity.Enum.CustomerRole;
import org.example.shelftoshelf.ExceptionManager.CustomerNotFoundException;
import org.example.shelftoshelf.Repo.BookRepo;
import org.example.shelftoshelf.Repo.CustomerRepo;
import org.example.shelftoshelf.config.CustomUserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerRepo customerRepo;
    private final BookRepo bookRepo;
    private final BookMaper bookMaper;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerMapper customerMapper,
                           CustomerRepo customerRepo,
                           BookRepo bookRepo,
                           BookMaper bookMaper,
                           OtpService otpService,
                           PasswordEncoder passwordEncoder) {
        this.customerMapper = customerMapper;
        this.customerRepo = customerRepo;
        this.bookRepo = bookRepo;
        this.bookMaper = bookMaper;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CustomerProfileResponseDto createAccount(CustomerRequestDto requestDto) {
        if (customerRepo.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email artıq qeydiyyatdan keçib: " + requestDto.getEmail());
        }

        Customer customer = customerMapper.toEntity(requestDto);
        customer.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        customer.setRole(CustomerRole.USER);
        customer.setVerified(false);

        Customer savedCustomer = customerRepo.save(customer);
        otpService.sendOtp(savedCustomer.getEmail());

        return customerMapper.toCustomerProfileResponseDto(savedCustomer);
    }

    @Transactional
    public CustomerProfileResponseDto verifyOtp(String email, String otp) {
        boolean correct = otpService.verifyOtp(email, otp);
        if (!correct) {
            throw new RuntimeException("OTP kodu yanlışdır");
        }

        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı"));

        customer.setVerified(true);
        Customer savedCustomer = customerRepo.save(customer);

        CustomerProfileResponseDto dto = customerMapper.toCustomerProfileResponseDto(savedCustomer);
        List<Book> books = bookRepo.findBySellerId(savedCustomer.getId());
        dto.setBooks(books.stream().map(bookMaper::toResponseDto).toList());
        dto.setBookCount(books.size());
        dto.setFavoriteBookIds(new HashSet<>(savedCustomer.getFavoriteBookIds()));
        return dto;
    }

    // İstifadəçinin öz profili (Daxil olmuş istifadəçi üçün)
    public CustomerProfileResponseDto getMyProfile() {
        CustomUserPrincipal principal = getCurrentPrincipal();
        Customer customer = customerRepo.findById(principal.getId())
                .orElseGet(() -> customerRepo.findByEmail(principal.getEmail())
                        .orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı")));

        List<Book> books = bookRepo.findBySellerId(customer.getId());
        CustomerProfileResponseDto dto = customerMapper.toCustomerProfileResponseDto(customer);
        dto.setBooks(books.stream().map(bookMaper::toResponseDto).toList());
        dto.setBookCount(books.size());
        dto.setFavoriteBookIds(new HashSet<>(customer.getFavoriteBookIds()));
        return dto;
    }

    // İstifadəçinin öz məlumatlarını yeniləməsi
    @Transactional
    public CustomerProfileResponseDto updateMyProfile(CustomerRequestDto requestDto) {
        CustomUserPrincipal principal = getCurrentPrincipal();
        Customer customer = customerRepo.findById(principal.getId())
                .orElseGet(() -> customerRepo.findByEmail(principal.getEmail())
                        .orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı")));

        customer.setName(requestDto.getName());
        customer.setSurname(requestDto.getSurname());
        customer.setEmail(requestDto.getEmail());
        customer.setPhone(requestDto.getPhone());

        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            customer.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        Customer updatedCustomer = customerRepo.save(customer);
        CustomerProfileResponseDto dto = customerMapper.toCustomerProfileResponseDto(updatedCustomer);
        List<Book> books = bookRepo.findBySellerId(customer.getId());
        dto.setBooks(books.stream().map(bookMaper::toResponseDto).toList());
        dto.setBookCount(books.size());
        dto.setFavoriteBookIds(new HashSet<>(updatedCustomer.getFavoriteBookIds()));
        return dto;
    }

    // ID üzrə profil
    public CustomerProfileResponseDto getCustomerProfile(Long customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı: ID " + customerId));

        List<Book> books = bookRepo.findBySellerId(customerId);
        CustomerProfileResponseDto dto = customerMapper.toCustomerProfileResponseDto(customer);
        dto.setBooks(books.stream().map(bookMaper::toResponseDto).toList());
        dto.setBookCount(books.size());
        dto.setFavoriteBookIds(new HashSet<>(customer.getFavoriteBookIds()));
        return dto;
    }

    // Başqa istifadəçilərin gördüyü ictimai profil və kitabları
    public CustomerPublicResponseDto getPublicProfile(Long customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı: ID " + customerId));

        List<Book> books = bookRepo.findBySellerIdAndStatus(customerId, BookStatus.ACTIVE);
        CustomerPublicResponseDto dto = customerMapper.toCustomerPublicResponseDto(customer);
        dto.setId(customer.getId());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setBooks(books.stream().map(bookMaper::toResponseDto).toList());
        return dto;
    }

    @Transactional
    public CustomerProfileResponseDto updateCustomer(Long customerId, CustomerRequestDto requestDto) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı: ID " + customerId));

        customer.setName(requestDto.getName());
        customer.setSurname(requestDto.getSurname());
        customer.setEmail(requestDto.getEmail());
        customer.setPhone(requestDto.getPhone());

        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            customer.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        Customer updatedCustomer = customerRepo.save(customer);
        CustomerProfileResponseDto dto = customerMapper.toCustomerProfileResponseDto(updatedCustomer);
        List<Book> books = bookRepo.findBySellerId(customerId);
        dto.setBooks(books.stream().map(bookMaper::toResponseDto).toList());
        dto.setBookCount(books.size());
        return dto;
    }

    // Wishlist (Sevimlilər) əlavə et / sil
    @Transactional
    public boolean toggleWishlist(Long bookId) {
        CustomUserPrincipal principal = getCurrentPrincipal();
        Customer customer = customerRepo.findById(principal.getId())
                .orElseGet(() -> customerRepo.findByEmail(principal.getEmail())
                        .orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı")));

        boolean isAdded;
        if (customer.getFavoriteBookIds().contains(bookId)) {
            customer.getFavoriteBookIds().remove(bookId);
            isAdded = false;
        } else {
            customer.getFavoriteBookIds().add(bookId);
            isAdded = true;
        }
        customerRepo.save(customer);
        return isAdded;
    }

    // İstifadəçinin sevimlilər siyahısı
    public List<BookResponseDto> getWishlist() {
        CustomUserPrincipal principal = getCurrentPrincipal();
        Customer customer = customerRepo.findById(principal.getId())
                .orElseGet(() -> customerRepo.findByEmail(principal.getEmail())
                        .orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı")));

        if (customer.getFavoriteBookIds() == null || customer.getFavoriteBookIds().isEmpty()) {
            return Collections.emptyList();
        }

        return bookRepo.findAllById(customer.getFavoriteBookIds()).stream()
                .map(bookMaper::toResponseDto)
                .toList();
    }

    // Bütün istifadəçilər (Yalnız ADMIN üçün - admin kitab detallarını görmür, yalnız adı, qiyməti, sayını görür)
    public List<CustomerProfileResponseDto> getAllCustomers() {
        return customerRepo.findAll().stream()
                .map(customer -> {
                    CustomerProfileResponseDto dto = customerMapper.toCustomerProfileResponseDto(customer);
                    List<Book> books = bookRepo.findBySellerId(customer.getId());
                    dto.setBookCount(books != null ? books.size() : 0);
                    if (books != null) {
                        dto.setBooks(books.stream().map(b -> {
                            BookResponseDto bDto = new BookResponseDto();
                            bDto.setId(b.getId());
                            bDto.setTitle(b.getTitle());
                            bDto.setPrice(b.getPrice());
                            bDto.setStockCount(b.getStockCount());
                            bDto.setStatus(b.getStatus());
                            return bDto;
                        }).toList());
                    } else {
                        dto.setBooks(Collections.emptyList());
                    }
                    return dto;
                })
                .toList();
    }

    // İstifadəçi silmək (Yalnız ADMIN üçün)
    @Transactional
    public void deleteCustomer(Long customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("İstifadəçi tapılmadı: ID " + customerId));
        customerRepo.delete(customer);
    }

    private CustomUserPrincipal getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Zəhmət olmasa daxil olun");
        }
        return (CustomUserPrincipal) auth.getPrincipal();
    }
}
