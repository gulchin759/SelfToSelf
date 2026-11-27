package org.example.shelftoshelf.config;

import org.example.shelftoshelf.Entity.Customer;
import org.example.shelftoshelf.Entity.Enum.CustomerRole;
import org.example.shelftoshelf.Entity.Store;
import org.example.shelftoshelf.Repo.CustomerRepo;
import org.example.shelftoshelf.Repo.StoreRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepo customerRepo;
    private final StoreRepo storeRepo;

    public CustomUserDetailsService(CustomerRepo customerRepo, StoreRepo storeRepo) {
        this.customerRepo = customerRepo;
        this.storeRepo = storeRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Check Customer
        Optional<Customer> customerOpt = customerRepo.findByEmail(email);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            CustomerRole role = customer.getRole() != null ? customer.getRole() : CustomerRole.USER;
            return new CustomUserPrincipal(
                    customer.getId(),
                    customer.getEmail(),
                    customer.getPassword(),
                    customer.getName(),
                    customer.getSurname(),
                    role,
                    customer.isVerified(),
                    false
            );
        }

        // 2. Check Store
        Optional<Store> storeOpt = storeRepo.findByEmail(email);
        if (storeOpt.isPresent()) {
            Store store = storeOpt.get();
            return new CustomUserPrincipal(
                    store.getId(),
                    store.getEmail(),
                    store.getPassword(),
                    store.getName(),
                    "",
                    CustomerRole.STORE,
                    store.isVerified(),
                    true
            );
        }

        throw new UsernameNotFoundException("İstifadəçi tapılmadı: " + email);
    }
}
