package org.example.shelftoshelf.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shelftoshelf.Entity.Enum.BookCondition;
import org.example.shelftoshelf.Entity.Enum.BookStatus;
import org.example.shelftoshelf.Entity.Enum.Genre;
import org.example.shelftoshelf.Entity.Enum.Language;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {

    private String title;
    private BigDecimal price;
    private String description;
    private List<String> authors = new ArrayList<>();
    private String isbn;
    private Integer pageCount;
    private Genre genre;
    private Language language;
    private String personalNote;
    private Integer stockCount;
    private BookCondition condition;
    private BookStatus status;
    private Long customerId;
    private Long storeId;
    private List<String> imageUrls = new ArrayList<>();


}