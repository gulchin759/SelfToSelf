package org.example.shelftoshelf.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shelftoshelf.Entity.Enum.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class BookResponseDto {
        private Long id;
        private String title;
        private BigDecimal price;
        private Integer stockCount;
        private String description;
        private List<String> authors;
        private String isbn;
        private Integer pageCount;
        private Genre genre;
        private Language language;
        private BookCondition condition;
        private String personalNote;
        private LocalDateTime publishedAt;
        private List<String> imageUrls;
        private String sellerName;
        private String storeName;
        private BookStatus status;


}
