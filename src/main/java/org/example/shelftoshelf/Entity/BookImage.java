package org.example.shelftoshelf.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor




@Entity
public class BookImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;


    @ManyToOne
    @JoinColumn(name = "book_id")
    private  Book book;


}
