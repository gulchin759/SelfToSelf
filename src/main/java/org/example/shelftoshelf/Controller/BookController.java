package org.example.shelftoshelf.Controller;

import org.example.shelftoshelf.Dto.Request.BookRequestDto;
import org.example.shelftoshelf.Dto.Response.BookResponseDto;
import org.example.shelftoshelf.Entity.Enum.*;
import org.example.shelftoshelf.Service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Bütün kitablar (hamı baxa bilər)
    @GetMapping
    public List<BookResponseDto> allBooks() {
        return bookService.allBooks();
    }

    // Tək bir kitabın detalları
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // İstifadəçinin yalnız öz əlavə etdiyi kitablar
    @GetMapping("/my-books")
    public List<BookResponseDto> getMyBooks() {
        return bookService.getMyBooks();
    }

    // Yeni kitab əlavə et (Daxil olmuş istifadəçi və ya mağaza)
    @PostMapping
    public BookResponseDto addBook(@RequestBody BookRequestDto requestDto) {
        return bookService.addBook(requestDto);
    }

    // Kitabı redaktə et (Yalnız sahibi və ya admin)
    @PutMapping("/{id}")
    public BookResponseDto updateBook(
            @PathVariable Long id,
            @RequestBody BookRequestDto requestDto) {
        return bookService.updateBook(id, requestDto);
    }

    // Kitabın statusunu dəyiş (ACTIVE, SOLD, INACTIVE)
    @PutMapping("/{id}/status")
    public BookResponseDto updateBookStatus(
            @PathVariable Long id,
            @RequestParam BookStatus status) {
        return bookService.updateBookStatus(id, status);
    }

    // Axtarış və filtrləmə
    @GetMapping("/search")
    public List<BookResponseDto> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) Language language,
            @RequestParam(required = false) BookCondition condition,
            @RequestParam(required = false) BookSortField sortBy,
            @RequestParam(required = false) SortDirection direction
    ) {
        return bookService.searchBooks(
                title,
                genre,
                language,
                condition,
                sortBy,
                direction
        );
    }

    // Kitab sil (Yalnız sahibi və ya admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
