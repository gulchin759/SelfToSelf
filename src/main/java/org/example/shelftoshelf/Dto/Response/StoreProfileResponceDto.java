package org.example.shelftoshelf.Dto.Response;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shelftoshelf.Entity.Book;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreProfileResponceDto {

    private Long id;
    private String name;
    private String location;
    private String phone;
    private  String email;
    private  String password;
    private List<Book> books ;

    public void setBooks(List<BookResponseDto> list) {
    }
}
