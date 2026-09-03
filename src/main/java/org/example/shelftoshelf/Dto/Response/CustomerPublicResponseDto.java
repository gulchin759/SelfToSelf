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

public class CustomerPublicResponseDto {
    private Long id;
    private String name;
    private String surname;
    private List<BookResponseDto> books;
}
