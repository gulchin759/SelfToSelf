package org.example.shelftoshelf.Service;

import org.example.shelftoshelf.Dto.Mapper.BookMaper;
import org.example.shelftoshelf.Dto.Mapper.StoreMapper;
import org.example.shelftoshelf.Dto.Request.StoreRequestDto;
import org.example.shelftoshelf.Dto.Response.BookResponseDto;
import org.example.shelftoshelf.Dto.Response.StoreProfileResponceDto;
import org.example.shelftoshelf.Dto.Response.StorePublicResponceDto;
import org.example.shelftoshelf.Entity.Book;
import org.example.shelftoshelf.Entity.Enum.BookStatus;
import org.example.shelftoshelf.Entity.Store;
import org.example.shelftoshelf.ExceptionManager.StoreNotFoundException;
import org.example.shelftoshelf.Repo.BookRepo;
import org.example.shelftoshelf.Repo.CustomerRepo;
import org.example.shelftoshelf.Repo.StoreRepo;
import org.example.shelftoshelf.config.CustomUserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StoreService {

    private final StoreMapper storeMapper;
    private final StoreRepo storeRepo;
    private final CustomerRepo customerRepo;
    private final BookRepo bookRepo;
    private final BookMaper bookMaper;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public StoreService(StoreMapper storeMapper,
                        StoreRepo storeRepo,
                        CustomerRepo customerRepo,
                        BookRepo bookRepo,
                        BookMaper bookMaper,
                        OtpService otpService,
                        PasswordEncoder passwordEncoder) {
        this.storeMapper = storeMapper;
        this.storeRepo = storeRepo;
        this.customerRepo = customerRepo;
        this.bookRepo = bookRepo;
        this.bookMaper = bookMaper;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    // Yalnız ADMIN yarada bilər
    @Transactional
    public StoreProfileResponceDto createStore(StoreRequestDto requestDto) {
        if (storeRepo.findByEmail(requestDto.getEmail()).isPresent() ||
            customerRepo.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email ilə artıq mağaza və ya istifadəçi mövcuddur: " + requestDto.getEmail());
        }

        Store store = storeMapper.toEntity(requestDto);
        store.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        store.setVerified(false); // Mağaza üçün də OTP təsdiqləməsi tələb olunur

        Store savedStore = storeRepo.save(store);
        otpService.sendOtp(savedStore.getEmail()); // OTP gmaile göndərilir

        StoreProfileResponceDto dto = storeMapper.toStoreProfileResponceDto(savedStore);
        dto.setBookCount(0);
        return dto;
    }

    public StoreProfileResponceDto verifyOtp(String email, String otp) {
        boolean correct = otpService.verifyOtp(email, otp);
        if (!correct) {
            throw new RuntimeException("OTP kodu yanlışdır");
        }

        Store store = storeRepo.findByEmail(email)
                .orElseThrow(() -> new StoreNotFoundException("Mağaza tapılmadı"));

        store.setVerified(true);
        Store savedStore = storeRepo.save(store);
        return storeMapper.toStoreProfileResponceDto(savedStore);
    }

    // Mağazanın öz profilinə və kitablarına baxması
    public StoreProfileResponceDto getMyStoreProfile() {
        CustomUserPrincipal principal = getCurrentPrincipal();
        Store store = storeRepo.findById(principal.getId())
                .orElseGet(() -> storeRepo.findByEmail(principal.getEmail())
                        .orElseThrow(() -> new StoreNotFoundException("Mağaza tapılmadı")));

        StoreProfileResponceDto dto = storeMapper.toStoreProfileResponceDto(store);
        List<Book> books = bookRepo.findByStoreId(store.getId());
        dto.setBooks(books.stream().map(bookMaper::toResponseDto).toList());
        dto.setBookCount(books.size());
        return dto;
    }

    // Mağazanın öz məlumatlarını redaktə etməsi
    @Transactional
    public StoreProfileResponceDto updateMyStore(StoreRequestDto requestDto) {
        CustomUserPrincipal principal = getCurrentPrincipal();
        Store store = storeRepo.findById(principal.getId())
                .orElseGet(() -> storeRepo.findByEmail(principal.getEmail())
                        .orElseThrow(() -> new StoreNotFoundException("Mağaza tapılmadı")));

        store.setName(requestDto.getName());
        store.setLocation(requestDto.getLocation());
        store.setPhone(requestDto.getPhone());
        store.setEmail(requestDto.getEmail());

        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            store.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        Store updated = storeRepo.save(store);
        StoreProfileResponceDto dto = storeMapper.toStoreProfileResponceDto(updated);
        List<Book> books = bookRepo.findByStoreId(updated.getId());
        dto.setBooks(books.stream().map(bookMaper::toResponseDto).toList());
        dto.setBookCount(books.size());
        return dto;
    }

    public StoreProfileResponceDto getMyProfile(Long id) {
        Store store = storeRepo.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("Mağaza tapılmadı: ID " + id));

        StoreProfileResponceDto dto = storeMapper.toStoreProfileResponceDto(store);
        List<Book> books = bookRepo.findByStoreId(id);
        dto.setBooks(books.stream().map(bookMaper::toResponseDto).toList());
        dto.setBookCount(books.size());
        return dto;
    }

    // İctimaiyyət üçün mağaza detalları və kitabları
    public StorePublicResponceDto getPublic(Long id) {
        Store store = storeRepo.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("Mağaza tapılmadı: ID " + id));

        StorePublicResponceDto dto = storeMapper.toStorePublicResponceDto(store);
        List<Book> books = bookRepo.findByStoreIdAndStatus(id, BookStatus.ACTIVE);
        dto.setBooks(books.stream().map(bookMaper::toResponseDto).toList());
        return dto;
    }

    @Transactional
    public StoreProfileResponceDto updateStore(Long id, StoreRequestDto requestDto) {
        Store store = storeRepo.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("Mağaza tapılmadı: ID " + id));

        store.setName(requestDto.getName());
        store.setLocation(requestDto.getLocation());
        store.setPhone(requestDto.getPhone());
        store.setEmail(requestDto.getEmail());

        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            store.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        Store updated = storeRepo.save(store);
        StoreProfileResponceDto dto = storeMapper.toStoreProfileResponceDto(updated);
        List<Book> books = bookRepo.findByStoreId(id);
        dto.setBooks(books.stream().map(bookMaper::toResponseDto).toList());
        dto.setBookCount(books.size());
        return dto;
    }

    @Transactional
    public void deleteStore(Long id) {
        Store store = storeRepo.findById(id)
                .orElseThrow(() -> new StoreNotFoundException("Mağaza tapılmadı: ID " + id));
        storeRepo.delete(store);
    }

    public List<StorePublicResponceDto> searchByName(String name) {
        return storeRepo.findByNameContainingIgnoreCase(name)
                .stream()
                .map(store -> {
                    StorePublicResponceDto dto = storeMapper.toStorePublicResponceDto(store);
                    dto.setBooks(bookRepo.findByStoreIdAndStatus(store.getId(), BookStatus.ACTIVE)
                            .stream().map(bookMaper::toResponseDto).toList());
                    return dto;
                })
                .toList();
    }

    public List<StoreProfileResponceDto> getAllStores() {
        // Bir mağaza digər mağazaları görməməlidir
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = false;
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
            if (principal.getRole() == org.example.shelftoshelf.Entity.Enum.CustomerRole.STORE) {
                return new ArrayList<>(); // Mağaza üçün digər mağazalar gizlədilir
            }
            if (principal.getRole() == org.example.shelftoshelf.Entity.Enum.CustomerRole.ADMIN) {
                isAdmin = true;
            }
        }

        final boolean forAdmin = isAdmin;
        return storeRepo.findAll()
                .stream()
                .map(store -> {
                    StoreProfileResponceDto dto = storeMapper.toStoreProfileResponceDto(store);
                    List<Book> books = bookRepo.findByStoreId(store.getId());
                    dto.setBookCount(books.size());
                    if (forAdmin) {
                        // Admin üçün yalnız kitabın adı, qiyməti, sayı və statusu (qısa xülasə, detallar yoxdur)
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
                        dto.setBooks(new ArrayList<>());
                    }
                    return dto;
                })
                .toList();
    }

    private CustomUserPrincipal getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Zəhmət olmasa daxil olun");
        }
        return (CustomUserPrincipal) auth.getPrincipal();
    }
}
