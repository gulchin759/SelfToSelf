package org.example.shelftoshelf.Service;

import org.example.shelftoshelf.Dto.Mapper.BookMaper;
import org.example.shelftoshelf.Dto.Mapper.StoreMapper;
import org.example.shelftoshelf.Dto.Request.StoreRequestDto;
import org.example.shelftoshelf.Dto.Response.StoreProfileResponceDto;
import org.example.shelftoshelf.Dto.Response.StorePublicResponceDto;
import org.example.shelftoshelf.Entity.Store;
import org.example.shelftoshelf.ExceptionManager.StoreNotFoundException;
import org.example.shelftoshelf.Repo.StoreRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {


    private final StoreMapper storeMapper;
    private final StoreRepo storeRepo;
    private final BookMaper bookMaper;
    private final OtpService otpService;

    public StoreService(StoreMapper storeMapper,
                        StoreRepo storeRepo,
                        BookMaper bookMaper,
                        OtpService otpService) {

        this.storeMapper = storeMapper;
        this.storeRepo = storeRepo;
        this.bookMaper = bookMaper;
        this.otpService = otpService;
    }


    public StoreProfileResponceDto createStore(StoreRequestDto requestDto) {
        Store store = storeMapper.toEntity(requestDto);
        Store savedStore = storeRepo.save(store);
        return storeMapper.toStoreProfileResponceDto(savedStore);
    }

    public StoreProfileResponceDto verifyOtp(String email, String otp) {

        boolean correct = otpService.verifyOtp(email, otp);

        if (!correct) {
            throw new RuntimeException("OTP kodu yanlışdır");
        }

        Store store = storeRepo.findByEmail(email)
                .orElseThrow(() ->
                        new StoreNotFoundException("Magaza tapilmadi"));


        store.setVerified(true);

        Store savedStore = storeRepo.save(store);

        return storeMapper.toStoreProfileResponceDto(savedStore);
    }


    public StoreProfileResponceDto getMyProfile(Long id) {
        Store store = storeRepo.findById(id).orElseThrow(() -> new StoreNotFoundException("Magaza tapilmadi"));
        StoreProfileResponceDto dto = storeMapper.toStoreProfileResponceDto(store);
        dto.setBooks(store.getBooks()
                        .stream()
                        .map(bookMaper::toResponseDto)
                        .toList()
        );
        return dto;
    }


    public  StorePublicResponceDto getPublic(Long id){
        Store store = storeRepo.findById(id).orElseThrow(() -> new StoreNotFoundException("Magaza tapilmadi"));
        StorePublicResponceDto dto = storeMapper.toStorePublicResponceDto(store);
        dto.setBooks(store.getBooks()
                .stream()
                .map(bookMaper::toResponseDto)
                .toList()
        );
        return  dto;
    }


    public StoreProfileResponceDto updateStore(Long id, StoreRequestDto requestDto) {
        Store store = storeRepo.findById(id).orElseThrow(() -> new StoreNotFoundException("Magaza tapilmadi"));
        store.setName(requestDto.getName());
        store.setLocation(requestDto.getLocation());
        store.setPhone(requestDto.getPhone());
        store.setEmail(requestDto.getEmail());
        store.setPassword(requestDto.getPassword());

        return storeMapper.toStoreProfileResponceDto(storeRepo.save(store));
    }

    public void deleteStore(Long id) {
        Store store = storeRepo.findById(id).orElseThrow(() -> new StoreNotFoundException("Magaza tapilmadi"));
        storeRepo.delete(store);
    }


    public List<StorePublicResponceDto> searchByName(String name) {

        return storeRepo.findByNameContainingIgnoreCase(name)
                .stream()
                .map(storeMapper::toStorePublicResponceDto)
                .toList();
    }

    public List<StoreProfileResponceDto> getAllStores() {

        return storeRepo.findAll()
                .stream()
                .map(storeMapper::toStoreProfileResponceDto)
                .toList();
    }





}
