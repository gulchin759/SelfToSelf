package org.example.shelftoshelf.Dto.Mapper;

import org.example.shelftoshelf.Dto.Request.CustomerRequestDto;
import org.example.shelftoshelf.Dto.Response.CustomerProfileResponseDto;
import org.example.shelftoshelf.Dto.Response.CustomerPublicResponseDto;
import org.example.shelftoshelf.Entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BookMaper.class})
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "storeName", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "favoriteBookIds", ignore = true)
    Customer toEntity(CustomerRequestDto requestDto);

    @Mapping(target = "books", ignore = true)
    @Mapping(target = "bookCount", ignore = true)
    CustomerProfileResponseDto toCustomerProfileResponseDto(Customer customer);

    @Mapping(target = "books", ignore = true)
    CustomerPublicResponseDto toCustomerPublicResponseDto(Customer customer);
}
