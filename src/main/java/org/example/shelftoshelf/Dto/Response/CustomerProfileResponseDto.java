package org.example.shelftoshelf.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shelftoshelf.Entity.Enum.CustomerRole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private CustomerRole role;
    private Integer bookCount;
    private List<BookResponseDto> books = new ArrayList<>();
    private Set<Long> favoriteBookIds = new HashSet<>();
}
