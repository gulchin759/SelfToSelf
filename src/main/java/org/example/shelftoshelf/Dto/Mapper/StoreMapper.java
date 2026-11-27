package org.example.shelftoshelf.Dto.Mapper;

import org.example.shelftoshelf.Dto.Request.StoreRequestDto;
import org.example.shelftoshelf.Dto.Response.StoreProfileResponceDto;
import org.example.shelftoshelf.Dto.Response.StorePublicResponceDto;
import org.example.shelftoshelf.Entity.Store;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BookMaper.class})
public interface StoreMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "books", ignore = true)
    Store toEntity(StoreRequestDto requestDto);

    @Mapping(target = "books", ignore = true)
    StorePublicResponceDto toStorePublicResponceDto(Store store);

    @Mapping(target = "books", ignore = true)
    @Mapping(target = "bookCount", ignore = true)
    StoreProfileResponceDto toStoreProfileResponceDto(Store store);
}
