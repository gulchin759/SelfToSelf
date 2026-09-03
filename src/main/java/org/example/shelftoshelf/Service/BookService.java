package org.example.shelftoshelf.Service;

import org.example.shelftoshelf.Dto.Mapper.BookMaper;
import org.example.shelftoshelf.Dto.Request.BookRequestDto;
import org.example.shelftoshelf.Dto.Response.BookResponseDto;
import org.example.shelftoshelf.Entity.Book;
import org.example.shelftoshelf.Entity.Enum.*;
import org.example.shelftoshelf.ExceptionManager.BookNotFoundException;
import org.example.shelftoshelf.Repo.BookRepo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookService {

    private final BookRepo bookRepo;
    private final BookMaper bookMaper;

    public BookService(BookRepo bookRepo, BookMaper bookMaper) {
        this.bookRepo = bookRepo;
        this.bookMaper = bookMaper;
    }

    //user ucun yazilanlar birci
    public List<BookResponseDto> allBooks() {
        return bookRepo.findAll().stream().map(book -> bookMaper.toResponseDto(book)).toList();
    }


    public BookResponseDto addBook(BookRequestDto requestDto) {
        Book book = bookMaper.toEntity(requestDto);
        book.setStatus(BookStatus.ACTIVE);
        Book savedBook = bookRepo.save(book);
        return bookMaper.toResponseDto(savedBook);
    }

    public BookResponseDto updateBook(Long id, BookRequestDto requestDto) {
        Book oldBook = bookMaper.toEntity(requestDto);
        Book thisbook = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException("Kitab tapilmadi "));

        thisbook.setTitle(oldBook.getTitle());
        thisbook.setPrice(oldBook.getPrice());
        thisbook.setDescription(oldBook.getDescription());
        thisbook.setIsbn(oldBook.getIsbn());
        thisbook.setPageCount(oldBook.getPageCount());
        thisbook.setGenre(oldBook.getGenre());
        thisbook.setLanguage(oldBook.getLanguage());
        thisbook.setPersonalNote(oldBook.getPersonalNote());
        thisbook.setStockCount(oldBook.getStockCount());
        thisbook.setCondition(oldBook.getCondition());

        Book savedBook = bookRepo.save(thisbook);
        return bookMaper.toResponseDto(savedBook);
    }

    public BookResponseDto updateBookStatus(Long id, BookRequestDto requestDto) {
        Book oldBook = bookMaper.toEntity(requestDto);
        Book newBook = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException("Kitab tapilmadi "));

        newBook.setStatus(oldBook.getStatus());
        Book savedBook = bookRepo.save(newBook);
        return bookMaper.toResponseDto(savedBook);
    }

    public List<BookResponseDto> searchBooks(String title, Genre genre, Language language, BookCondition condition, BookSortField sortBy, SortDirection direction) {
        Specification<Book> bookSpecification = Specification.where((Specification<Book>) null);

        if (title != null && !title.isBlank()) {
            bookSpecification = bookSpecification.and(
                    (root, query, cb) ->
                            cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%")
            );
        }

        if (genre != null) {
            bookSpecification = bookSpecification.and(
                    (root, query, cb) ->
                            cb.equal(root.get("genre"), genre)
            );
        }

        if (language != null) {
            bookSpecification = bookSpecification.and(
                    (root, query, cb) ->
                            cb.equal(root.get("language"), language)
            );
        }

        if (condition != null) {
            bookSpecification = bookSpecification.and(
                    (root, query, cb) ->
                            cb.equal(root.get("condition"), condition)
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

        if (books.isEmpty()) {
            throw new BookNotFoundException("Axtarışa uyğun kitab tapılmadı");
        }

        return books.stream()
                .map(bookMaper::toResponseDto)
                .toList();
    }





    //admi ucucn xususinolaraq
    public  void deleteBook( Long id){
        Book book=bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException("Kitab tapilmadi "));
        bookRepo.delete(book);
    }

}
