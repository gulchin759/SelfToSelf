package org.example.shelftoshelf.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shelftoshelf.Entity.Book;

import javax.management.relation.Role;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileResponseDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private List<Book> books;
    private Role role;

    public void setBooks(List<BookResponseDto> list) {

    }
}
