package org.example.shelftoshelf.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shelftoshelf.Entity.Enum.CustomerRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor




@Entity
public class Customer {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private String surname;

        private String phone;
        private String email;
        private String password;


        @Enumerated(EnumType.STRING)
        private CustomerRole role;
        private String storeName;


        @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Book> books = new ArrayList<>();




}
