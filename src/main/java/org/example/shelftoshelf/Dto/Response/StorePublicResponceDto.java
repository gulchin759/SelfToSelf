package org.example.shelftoshelf.Dto.Response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorePublicResponceDto {
    private String name;
    private String location;
    private String phone;

    public void setBooks(List<BookResponseDto> list) {
    }
}
