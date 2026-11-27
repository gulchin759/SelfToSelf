package org.example.shelftoshelf.Service;

import org.example.shelftoshelf.Dto.Mapper.BookMaper;
import org.example.shelftoshelf.Dto.Request.BookRequestDto;
import org.example.shelftoshelf.Dto.Response.BookResponseDto;
import org.example.shelftoshelf.Entity.Book;
import org.example.shelftoshelf.Entity.BookImage;
import org.example.shelftoshelf.Entity.Customer;
import org.example.shelftoshelf.Entity.Enum.*;
import org.example.shelftoshelf.Entity.Store;
import org.example.shelftoshelf.ExceptionManager.BookNotFoundException;
import org.example.shelftoshelf.Repo.BookRepo;
import org.example.shelftoshelf.Repo.CustomerRepo;
import org.example.shelftoshelf.Repo.StoreRepo;
import org.example.shelftoshelf.config.CustomUserPrincipal;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private final BookRepo bookRepo;
    private final BookMaper bookMaper;
    private final CustomerRepo customerRepo;
    private final StoreRepo storeRepo;

    public BookService(BookRepo bookRepo, BookMaper bookMaper, CustomerRepo customerRepo, StoreRepo storeRepo) {
        this.bookRepo = bookRepo;
        this.bookMaper = bookMaper;
        this.customerRepo = customerRepo;
        this.storeRepo = storeRepo;
    }

    public List<BookResponseDto> allBooks() {
        return bookRepo.findAll().stream()
                .map(bookMaper::toResponseDto)
                .toList();
    }

    public BookResponseDto getBookById(Long id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Kitab tapılmadı: ID " + id));
        return bookMaper.toResponseDto(book);
    }

    // İstifadəçinin yalnız öz əlavə etdiyi kitablar
    public List<BookResponseDto> getMyBooks() {
        CustomUserPrincipal principal = getCurrentPrincipal();
        List<Book> books;

        if (principal.getRole() == CustomerRole.STORE) {
            books = bookRepo.findByStoreId(principal.getId());
        } else {
            books = bookRepo.findBySellerId(principal.getId());
        }

        return books.stream()
                .map(bookMaper::toResponseDto)
                .toList();
    }

    @Transactional
    public BookResponseDto addBook(BookRequestDto requestDto) {
        CustomUserPrincipal principal = getCurrentPrincipal();

        if (principal.getRole() == CustomerRole.ADMIN) {
            throw new AccessDeniedException("Admin kitab əlavə edə bilməz! Kitabları yalnız istifadəçilər və mağazalar əlavə edə bilər.");
        }

        Book book = bookMaper.toEntity(requestDto);
        book.setStatus(BookStatus.ACTIVE);

        if (requestDto.getAuthors() != null) {
            book.setAuthors(new ArrayList<>(requestDto.getAuthors()));
        }

        // Link with current authenticated user or store
        if (principal.getRole() == CustomerRole.STORE) {
            Store store = storeRepo.findById(principal.getId())
                    .orElseGet(() -> storeRepo.findByEmail(principal.getEmail()).orElse(null));
            book.setStore(store);
        } else if (principal.getRole() == CustomerRole.ADMIN && requestDto.getStoreId() != null) {
            Store store = storeRepo.findById(requestDto.getStoreId()).orElse(null);
            book.setStore(store);
        } else {
            Customer seller = customerRepo.findById(principal.getId())
                    .orElseGet(() -> customerRepo.findByEmail(principal.getEmail()).orElse(null));
            book.setSeller(seller);
        }

        // Attach images
        if (requestDto.getImageUrls() != null && !requestDto.getImageUrls().isEmpty()) {
            for (String url : requestDto.getImageUrls()) {
                if (url != null && !url.isBlank()) {
                    book.addImage(new BookImage(url.trim(), book));
                }
            }
        }


        if (requestDto.getStatus() != null) {
            book.setStatus(requestDto.getStatus());
        }

        Book savedBook = bookRepo.save(book);
        return bookMaper.toResponseDto(savedBook);
    }

    @Transactional
    public BookResponseDto updateBook(Long id, BookRequestDto requestDto) {
        Book thisbook = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Kitab tapılmadı: ID " + id));

        verifyOwnershipOrAdmin(thisbook);

        thisbook.setTitle(requestDto.getTitle());
        thisbook.setPrice(requestDto.getPrice());
        thisbook.setDescription(requestDto.getDescription());
        thisbook.setIsbn(requestDto.getIsbn());
        thisbook.setPageCount(requestDto.getPageCount());
        thisbook.setGenre(requestDto.getGenre());
        thisbook.setLanguage(requestDto.getLanguage());
        thisbook.setPersonalNote(requestDto.getPersonalNote());
        thisbook.setStockCount(requestDto.getStockCount());
        thisbook.setCondition(requestDto.getCondition());

        if (requestDto.getStatus() != null) {
            thisbook.setStatus(requestDto.getStatus());
        }


        if (requestDto.getAuthors() != null) {
            thisbook.setAuthors(new ArrayList<>(requestDto.getAuthors()));
        }

        if (requestDto.getImageUrls() != null && !requestDto.getImageUrls().isEmpty()) {
            thisbook.getBookImages().clear();
            for (String url : requestDto.getImageUrls()) {
                if (url != null && !url.isBlank()) {
                    thisbook.addImage(new BookImage(url.trim(), thisbook));
                }
            }
        }

        Book savedBook = bookRepo.save(thisbook);
        return bookMaper.toResponseDto(savedBook);
    }

    @Transactional
    public BookResponseDto updateBookStatus(Long id, BookStatus newStatus) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Kitab tapılmadı: ID " + id));

        verifyOwnershipOrAdmin(book);

        book.setStatus(newStatus != null ? newStatus : BookStatus.ACTIVE);
        Book savedBook = bookRepo.save(book);
        return bookMaper.toResponseDto(savedBook);
    }

    public List<BookResponseDto> searchBooks(String title, Genre genre, Language language,
                                             BookCondition condition, BookSortField sortBy,
                                             SortDirection direction) {

        Specification<Book> bookSpecification = (root, query, cb) -> cb.conjunction();

        if (title != null && !title.isBlank()) {
            bookSpecification = bookSpecification.and(
                    (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%")
            );
        }

        if (genre != null) {
            bookSpecification = bookSpecification.and(
                    (root, query, cb) -> cb.equal(root.get("genre"), genre)
            );
        }

        if (language != null) {
            bookSpecification = bookSpecification.and(
                    (root, query, cb) -> cb.equal(root.get("language"), language)
            );
        }

        if (condition != null) {
            bookSpecification = bookSpecification.and(
                    (root, query, cb) -> cb.equal(root.get("condition"), condition)
            );
        }

        Sort sort = Sort.unsorted();

        if (sortBy != null) {
            String field = switch (sortBy) {
                case PRICE -> "price";
                case PUBLISHED_AT -> "publishedAt";
            };

            Sort.Direction sortDirection = direction == SortDirection.DESC
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            sort = Sort.by(sortDirection, field);
        }

        List<Book> books = bookRepo.findAll(bookSpecification, sort);

        return books.stream()
                .map(bookMaper::toResponseDto)
                .toList();
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Kitab tapılmadı: ID " + id));

        verifyOwnershipOrAdmin(book);
        bookRepo.delete(book);
    }

    private void verifyOwnershipOrAdmin(Book book) {
        CustomUserPrincipal principal = getCurrentPrincipal();
        if (principal.getRole() == CustomerRole.ADMIN) {
            return;
        }

        if (book.getSeller() != null && book.getSeller().getId().equals(principal.getId())) {
            return;
        }

        if (book.getStore() != null && book.getStore().getId().equals(principal.getId())) {
            return;
        }

        throw new AccessDeniedException("Bu kitab üzərində əməliyyat aparmaq üçün icazəniz yoxdur!");
    }

    private CustomUserPrincipal getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Zəhmət olmasa əvvəlcə daxil olun");
        }
        return (CustomUserPrincipal) auth.getPrincipal();
    }
}
