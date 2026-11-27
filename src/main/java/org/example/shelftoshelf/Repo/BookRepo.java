package org.example.shelftoshelf.Repo;

import org.example.shelftoshelf.Entity.Book;
import org.example.shelftoshelf.Entity.Enum.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findBySellerId(Long sellerId);

    List<Book> findBySellerIdAndStatus(Long sellerId, BookStatus status);

    List<Book> findByStoreId(Long storeId);

    List<Book> findByStoreIdAndStatus(Long storeId, BookStatus status);
}
