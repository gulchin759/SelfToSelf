package org.example.shelftoshelf.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shelftoshelf.Entity.Enum.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor




@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private  String title;
    private BigDecimal price;

    @OneToMany(mappedBy = "book",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookImage> bookImages=new ArrayList<>();

    public void addImage(BookImage image){
        bookImages.add(image);
        image.setBook(this);
    }

    public void removeImage(BookImage image) {
        bookImages.remove(image);
        image.setBook(null);
    }


    private LocalDateTime publishedAt;

    @PrePersist
    public void prePersist() {
        this.publishedAt = LocalDateTime.now();
    }

    private String description;

    @ElementCollection
    private List<String> authors = new ArrayList<>();
    private String isbn;
    private Integer pageCount;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Enumerated(EnumType.STRING)
    private Language language;

    private String personalNote;

    private Integer stockCount ;

    @Enumerated(EnumType.STRING)
    private BookCondition condition;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer seller;


    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;









}
