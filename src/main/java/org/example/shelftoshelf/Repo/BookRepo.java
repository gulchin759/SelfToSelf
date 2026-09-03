package org.example.shelftoshelf.Repo;

import org.example.shelftoshelf.Entity.Book;
import org.example.shelftoshelf.Entity.Enum.BookStatus;
import org.example.shelftoshelf.Entity.Enum.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo  extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findBySellerIdAndStatus(Long sellerId, BookStatus status);
}
