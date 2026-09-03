package org.example.shelftoshelf.Repo;

import org.example.shelftoshelf.Entity.Book;
import org.example.shelftoshelf.Entity.Customer;
import org.example.shelftoshelf.Entity.Enum.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepo extends JpaRepository<Customer ,Long>{


}
