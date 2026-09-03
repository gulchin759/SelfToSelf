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

    public StoreService(StoreMapper storeMapper, StoreRepo storeRepo,BookMaper bookMaper) {
        this.storeMapper = storeMapper;
        this.storeRepo = storeRepo;
        this.bookMaper=bookMaper;
    }


    public StoreProfileResponceDto createStore(StoreRequestDto requestDto) {
        Store store = storeMapper.toEntity(requestDto);
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


    public StoreProfileResponceDto updateStore(Long id,StoreRequestDto requestDto){
        Store storeOld=storeMapper.toEntity(requestDto);
        Store newStore=storeRepo.findById(id).orElseThrow(() -> new StoreNotFoundException("Magaza tapilmadi"));

        newStore.setBooks(storeOld.getBooks());
        newStore.setEmail(storeOld.getEmail());
        newStore.setName(storeOld.getName());
        newStore.setPhone(storeOld.getPhone());
        newStore.setPassword(storeOld.getPassword());

        Store savedStore = storeRepo.save(newStore);
        return  storeMapper.toStoreProfileResponceDto(savedStore);
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
