package org.example.shelftoshelf.Dto.Mapper;

import org.example.shelftoshelf.Dto.Request.CustomerRequestDto;
import org.example.shelftoshelf.Dto.Response.CustomerProfileResponseDto;
import org.example.shelftoshelf.Dto.Response.CustomerPublicResponseDto;
import org.example.shelftoshelf.Entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity( CustomerRequestDto requestDto);
    CustomerProfileResponseDto toCustomerProfileResponseDto(Customer customer);

    CustomerPublicResponseDto toCustomerPublicResponseDto(Customer customer);

}
