package org.example.shelftoshelf.config;

import org.example.shelftoshelf.Entity.Book;
import org.example.shelftoshelf.Entity.BookImage;
import org.example.shelftoshelf.Entity.Customer;
import org.example.shelftoshelf.Entity.Enum.*;
import org.example.shelftoshelf.Entity.Store;
import org.example.shelftoshelf.Repo.BookRepo;
import org.example.shelftoshelf.Repo.CustomerRepo;
import org.example.shelftoshelf.Repo.StoreRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final CustomerRepo customerRepo;
    private final StoreRepo storeRepo;
    private final BookRepo bookRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CustomerRepo customerRepo,
                           StoreRepo storeRepo,
                           BookRepo bookRepo,
                           PasswordEncoder passwordEncoder) {
        this.customerRepo = customerRepo;
        this.storeRepo = storeRepo;
        this.bookRepo = bookRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Initialize Admin
        Customer admin = customerRepo.findByEmail("admin@shelftoshelf.com").orElse(null);
        if (admin == null) {
            admin = new Customer();
            admin.setName("Sistem");
            admin.setSurname("Admini");
            admin.setEmail("admin@shelftoshelf.com");
            admin.setPhone("+994 50 000 00 00");
            admin.setRole(CustomerRole.ADMIN);
            admin.setVerified(true);
        }
        admin.setPassword(passwordEncoder.encode("admin123"));
        customerRepo.save(admin);

        System.out.println("==========================================================");
        System.out.println(">>> [SHELF TO SHELF] ADMIN GİRİŞ MƏLUMATLARI (SOUT) <<<");
        System.out.println("ADMIN EMAIL : admin@shelftoshelf.com");
        System.out.println("ADMIN ŞİFRƏ : admin123");
        System.out.println("ROL         : ADMIN");
        System.out.println("==========================================================");

        // 2. Initialize Default Store
        Store sampleStore = storeRepo.findByEmail("kitabevi@shelftoshelf.com").orElse(null);
        if (sampleStore == null) {
            sampleStore = new Store();
            sampleStore.setName("Ali və Nino Kitab Evi");
            sampleStore.setLocation("Bakı şəhəri, Nizami küçəsi 45");
            sampleStore.setPhone("+994 12 493 00 00");
            sampleStore.setEmail("kitabevi@shelftoshelf.com");
            sampleStore.setPassword(passwordEncoder.encode("store123"));
            sampleStore.setVerified(true);
            sampleStore = storeRepo.save(sampleStore);
            log.info("Default Mağaza yaradıldı: kitabevi@shelftoshelf.com / store123");
        }

        // 3. Initialize Default User
        Customer user = customerRepo.findByEmail("user@shelftoshelf.com").orElse(null);
        if (user == null) {
            user = new Customer();
            user.setName("Gülçin");
            user.setSurname("Mirzəyeva");
            user.setEmail("user@shelftoshelf.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setPhone("+994 55 123 45 67");
            user.setRole(CustomerRole.USER);
            user.setVerified(true);
            user = customerRepo.save(user);
            log.info("Default İstifadəçi yaradıldı: user@shelftoshelf.com / user123");
        }

        // 4. Initialize sample books if none exist
        if (bookRepo.count() == 0) {
            Book b1 = new Book();
            b1.setTitle("1984");
            b1.setAuthors(List.of("George Orwell"));
            b1.setPrice(new BigDecimal("14.50"));
            b1.setDescription("Distopik dünya və totalitar rejimin dəhşətləri haqqında şah əsər.");
            b1.setIsbn("978-0451524935");
            b1.setPageCount(328);
            b1.setGenre(Genre.DRAMA);
            b1.setLanguage(Language.AZERBAIJANI);
            b1.setCondition(BookCondition.LIKE_NEW);
            b1.setStatus(BookStatus.ACTIVE);
            b1.setStockCount(3);
            b1.setSeller(user);
            b1.addImage(new BookImage("https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=600&q=80", b1));
            bookRepo.save(b1);

            Book b2 = new Book();
            b2.setTitle("Cinayət və Cəza");
            b2.setAuthors(List.of("Fyodor Dostoyevski"));
            b2.setPrice(new BigDecimal("18.00"));
            b2.setDescription("Raskolnikovun daxili iztirabları və mənəvi dirçəlişi.");
            b2.setIsbn("978-0140449136");
            b2.setPageCount(671);
            b2.setGenre(Genre.DRAMA);
            b2.setLanguage(Language.AZERBAIJANI);
            b2.setCondition(BookCondition.LIKE_NEW);
            b2.setStatus(BookStatus.ACTIVE);
            b2.setStockCount(2);
            b2.setSeller(user);
            b2.addImage(new BookImage("https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=600&q=80", b2));
            bookRepo.save(b2);

            Book b3 = new Book();
            b3.setTitle("Səfillər");
            b3.setAuthors(List.of("Viktor Hüqo"));
            b3.setPrice(new BigDecimal("25.00"));
            b3.setDescription("Ədalət, mərhəmət və sevginin təntənəsi.");
            b3.setIsbn("978-0140444308");
            b3.setPageCount(1200);
            b3.setGenre(Genre.HISTORY);
            b3.setLanguage(Language.AZERBAIJANI);
            b3.setCondition(BookCondition.GOOD);
            b3.setStatus(BookStatus.ACTIVE);
            b3.setStockCount(5);
            b3.setStore(sampleStore);
            b3.addImage(new BookImage("https://images.unsplash.com/photo-1543002588-bfa74002ed7e?auto=format&fit=crop&w=600&q=80", b3));
            bookRepo.save(b3);

            log.info("Nümunə kitablar uğurla əlavə edildi.");
        }
    }
}
