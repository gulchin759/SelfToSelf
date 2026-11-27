package org.example.shelftoshelf.Dto.Mapper;

import org.example.shelftoshelf.Dto.Request.BookRequestDto;
import org.example.shelftoshelf.Dto.Response.BookResponseDto;
import org.example.shelftoshelf.Entity.Book;
import org.example.shelftoshelf.Entity.BookImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMaper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookImages", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "store", ignore = true)
    Book toEntity(BookRequestDto requestDto);

    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerName", expression = "java(book.getSeller() != null ? (book.getSeller().getName() + (book.getSeller().getSurname() != null ? \" \" + book.getSeller().getSurname() : \"\")) : null)")
    @Mapping(target = "sellerEmail", source = "seller.email")
    @Mapping(target = "sellerPhone", source = "seller.phone")
    @Mapping(target = "storeId", source = "store.id")
    @Mapping(target = "storeName", source = "store.name")
    @Mapping(target = "imageUrls", source = "bookImages", qualifiedByName = "mapImagesToUrls")
    BookResponseDto toResponseDto(Book book);

    @Named("mapImagesToUrls")
    default List<String> mapImagesToUrls(List<BookImage> images) {
        if (images == null) {
            return Collections.emptyList();
        }
        return images.stream()
                .filter(img -> img.getUrl() != null && !img.getUrl().isBlank())
                .map(BookImage::getUrl)
                .toList();
    }
}
