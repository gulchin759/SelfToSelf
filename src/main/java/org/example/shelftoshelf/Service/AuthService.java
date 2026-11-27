package org.example.shelftoshelf.Service;

import org.example.shelftoshelf.Dto.Request.AuthRequestDto;
import org.example.shelftoshelf.Dto.Request.CustomerRequestDto;
import org.example.shelftoshelf.Dto.Request.OtpVerifyRequestDto;
import org.example.shelftoshelf.Dto.Response.AuthResponseDto;
import org.example.shelftoshelf.Entity.Customer;
import org.example.shelftoshelf.Entity.Enum.CustomerRole;
import org.example.shelftoshelf.Entity.Store;
import org.example.shelftoshelf.Repo.CustomerRepo;
import org.example.shelftoshelf.Repo.StoreRepo;
import org.example.shelftoshelf.config.CustomUserPrincipal;
import org.example.shelftoshelf.config.JwtService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final CustomerRepo customerRepo;
    private final StoreRepo storeRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;

    public AuthService(CustomerRepo customerRepo,
                       StoreRepo storeRepo,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       OtpService otpService,
                       AuthenticationManager authenticationManager) {
        this.customerRepo = customerRepo;
        this.storeRepo = storeRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponseDto register(CustomerRequestDto requestDto) {
        if (customerRepo.findByEmail(requestDto.getEmail()).isPresent() ||
            storeRepo.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email artıq qeydiyyatdan keçib: " + requestDto.getEmail());
        }

        Customer customer = new Customer();
        customer.setName(requestDto.getName());
        customer.setSurname(requestDto.getSurname());
        customer.setEmail(requestDto.getEmail());
        customer.setPhone(requestDto.getPhone());
        customer.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        customer.setRole(CustomerRole.USER);
        customer.setVerified(false);

        Customer savedCustomer = customerRepo.save(customer);

        // Send OTP
        otpService.sendOtp(savedCustomer.getEmail());

        return AuthResponseDto.builder()
                .id(savedCustomer.getId())
                .email(savedCustomer.getEmail())
                .name(savedCustomer.getName())
                .surname(savedCustomer.getSurname())
                .role(CustomerRole.USER)
                .message("Qeydiyyat uğurla başladı! Email ünvanınıza 6 rəqəmli OTP təsdiqləmə kodu göndərildi.")
                .build();
    }

    @Transactional
    public AuthResponseDto verifyOtp(OtpVerifyRequestDto requestDto) {
        String email = requestDto.getEmail();
        String otp = requestDto.getOtp();

        boolean verified = otpService.verifyOtp(email, otp);
        if (!verified) {
            throw new RuntimeException("Daxil edilən OTP kodu yanlışdır");
        }

        Optional<Customer> customerOpt = customerRepo.findByEmail(email);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setVerified(true);
            Customer saved = customerRepo.save(customer);

            CustomUserPrincipal principal = new CustomUserPrincipal(
                    saved.getId(),
                    saved.getEmail(),
                    saved.getPassword(),
                    saved.getName(),
                    saved.getSurname(),
                    saved.getRole() != null ? saved.getRole() : CustomerRole.USER,
                    true,
                    false
            );

            String token = jwtService.generateToken(principal, saved.getId(), saved.getRole().name(), saved.getName());

            return AuthResponseDto.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .id(saved.getId())
                    .email(saved.getEmail())
                    .name(saved.getName())
                    .surname(saved.getSurname())
                    .role(saved.getRole())
                    .message("Hesabınız uğurla təsdiqləndi!")
                    .build();
        }

        Optional<Store> storeOpt = storeRepo.findByEmail(email);
        if (storeOpt.isPresent()) {
            Store store = storeOpt.get();
            store.setVerified(true);
            Store savedStore = storeRepo.save(store);

            CustomUserPrincipal principal = new CustomUserPrincipal(
                    savedStore.getId(),
                    savedStore.getEmail(),
                    savedStore.getPassword(),
                    savedStore.getName(),
                    "",
                    CustomerRole.STORE,
                    true,
                    true
            );

            String token = jwtService.generateToken(principal, savedStore.getId(), CustomerRole.STORE.name(), savedStore.getName());

            return AuthResponseDto.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .id(savedStore.getId())
                    .email(savedStore.getEmail())
                    .name(savedStore.getName())
                    .role(CustomerRole.STORE)
                    .message("Mağaza hesabı uğurla təsdiqləndi!")
                    .build();
        }

        throw new RuntimeException("Hesab tapılmadı");
    }

    public void resendOtp(String email) {
        boolean exists = customerRepo.findByEmail(email).isPresent() || storeRepo.findByEmail(email).isPresent();
        if (!exists) {
            throw new RuntimeException("Bu email ilə qeydiyyat tapılmadı");
        }
        otpService.sendOtp(email);
    }

    // Generic login
    public AuthResponseDto login(AuthRequestDto requestDto) {
        return executeLogin(requestDto, null);
    }

    // Dedicated User login
    public AuthResponseDto userLogin(AuthRequestDto requestDto) {
        return executeLogin(requestDto, CustomerRole.USER);
    }

    // Dedicated Store login
    public AuthResponseDto storeLogin(AuthRequestDto requestDto) {
        return executeLogin(requestDto, CustomerRole.STORE);
    }

    // Dedicated Admin login
    public AuthResponseDto adminLogin(AuthRequestDto requestDto) {
        return executeLogin(requestDto, CustomerRole.ADMIN);
    }

    private AuthResponseDto executeLogin(AuthRequestDto requestDto, CustomerRole expectedRole) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Email və ya şifrə yanlışdır");
        }

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        if (!principal.isEnabled()) {
            throw new RuntimeException("Hesabınız hələ təsdiqlənməyib! Zəhmət olmasa emailinizə göndərilən OTP kodu ilə təsdiqləyin.");
        }

        if (expectedRole != null && principal.getRole() != expectedRole) {
            String roleName = switch (expectedRole) {
                case ADMIN -> "Admin";
                case STORE -> "Mağaza";
                case USER -> "İstifadəçi";
            };
            throw new AccessDeniedException("Bu giriş bölməsi yalnız " + roleName + " üçündür!");
        }

        String token = jwtService.generateToken(
                principal,
                principal.getId(),
                principal.getRole().name(),
                principal.getName()
        );

        return AuthResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .id(principal.getId())
                .email(principal.getEmail())
                .name(principal.getName())
                .surname(principal.getSurname())
                .role(principal.getRole())
                .message("Giriş uğurla tamamlandı")
                .build();
    }

    public AuthResponseDto getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("İstifadəçi daxil olmayıb");
        }

        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        return AuthResponseDto.builder()
                .id(principal.getId())
                .email(principal.getEmail())
                .name(principal.getName())
                .surname(principal.getSurname())
                .role(principal.getRole())
                .build();
    }
}
