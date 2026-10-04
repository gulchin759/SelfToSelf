package org.example.shelftoshelf.Repo;


import org.example.shelftoshelf.Entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepo extends JpaRepository<Store,Long> {

    List<Store> findByNameContainingIgnoreCase(String name);
    Optional<Store> findByEmail(String email);
}
