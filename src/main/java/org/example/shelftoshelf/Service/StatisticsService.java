package org.example.shelftoshelf.Service;

import org.example.shelftoshelf.Dto.Response.StatisticsResponseDto;
import org.example.shelftoshelf.Repo.BookRepo;
import org.example.shelftoshelf.Repo.CustomerRepo;
import org.example.shelftoshelf.Repo.StoreRepo;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final BookRepo bookRepo;
    private final CustomerRepo customerRepo;
    private final StoreRepo storeRepo;

    public StatisticsService(BookRepo bookRepo,
                             CustomerRepo customerRepo,
                             StoreRepo storeRepo) {
        this.bookRepo = bookRepo;
        this.customerRepo = customerRepo;
        this.storeRepo = storeRepo;
    }

    public StatisticsResponseDto getStatistics() {

        StatisticsResponseDto dto = new StatisticsResponseDto();

        dto.setBookCount(bookRepo.count());
        dto.setCustomerCount(customerRepo.count());
        dto.setStoreCount(storeRepo.count());

        return dto;
    }
}
