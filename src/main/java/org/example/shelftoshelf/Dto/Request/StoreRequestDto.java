package org.example.shelftoshelf.Dto.Request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequestDto {

    private String name;
    private String location;
    private String phone;
    private String email;
    private String password;
}
