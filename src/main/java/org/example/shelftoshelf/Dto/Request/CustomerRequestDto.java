package org.example.shelftoshelf.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CustomerRequestDto {


    private String name;
    private String surname;

    private String phone;
    private String email;
    private String password;



}
