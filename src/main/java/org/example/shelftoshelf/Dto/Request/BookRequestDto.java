package org.example.shelftoshelf.Dto.Request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shelftoshelf.Entity.Enum.BookCondition;
import org.example.shelftoshelf.Entity.Enum.Genre;
import org.example.shelftoshelf.Entity.Enum.Language;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {

    private String title;
    private BigDecimal price;
    private String description;
    private List<String> authors;
    private String isbn;
    private Integer pageCount;
    private Genre genre;
    private Language language;
    private String personalNote;
    private Integer stockCount;
    private BookCondition condition;
    private Long customerId;
    private Long storeId;
}