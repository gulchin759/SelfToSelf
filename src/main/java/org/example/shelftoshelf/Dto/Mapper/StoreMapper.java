package org.example.shelftoshelf.Dto.Mapper;

import org.example.shelftoshelf.Dto.Request.StoreRequestDto;
import org.example.shelftoshelf.Dto.Response.StoreProfileResponceDto;
import org.example.shelftoshelf.Dto.Response.StorePublicResponceDto;
import org.example.shelftoshelf.Entity.Store;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    Store toEntity(StoreRequestDto requestDto);

    StorePublicResponceDto toStorePublicResponceDto(Store store);
    StoreProfileResponceDto toStoreProfileResponceDto(Store store);


}
