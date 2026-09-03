package org.example.shelftoshelf.Dto.Mapper;

import org.example.shelftoshelf.Dto.Request.BookRequestDto;
import org.example.shelftoshelf.Dto.Response.BookResponseDto;
import org.example.shelftoshelf.Entity.Book;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface BookMaper {
    Book toEntity(BookRequestDto requestDto);
    BookResponseDto toResponseDto(Book book);
}
