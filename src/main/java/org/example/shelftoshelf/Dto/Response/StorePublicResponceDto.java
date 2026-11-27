package org.example.shelftoshelf.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorePublicResponceDto {
    private Long id;
    private String name;
    private String location;
    private String phone;
    private String email;
    private List<BookResponseDto> books = new ArrayList<>();
}
