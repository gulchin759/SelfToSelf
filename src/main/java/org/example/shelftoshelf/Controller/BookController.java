package org.example.shelftoshelf.Controller;

import org.example.shelftoshelf.Dto.Request.BookRequestDto;
import org.example.shelftoshelf.Dto.Response.BookResponseDto;
import org.example.shelftoshelf.Entity.Enum.*;
import org.example.shelftoshelf.Service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }


    @GetMapping
    public List<BookResponseDto> allBooks() {
        return bookService.allBooks();
    }


    @PostMapping
    public BookResponseDto addBook(@RequestBody BookRequestDto requestDto) {
        return bookService.addBook(requestDto);
    }


    @PutMapping("/{id}")
    public BookResponseDto updateBook(
            @PathVariable Long id,
            @RequestBody BookRequestDto requestDto) {
        return bookService.updateBook(id, requestDto);
    }

    @PutMapping("/{id}/status")
    public BookResponseDto updateBookStatus(
            @PathVariable Long id,
            @RequestBody BookRequestDto requestDto) {
        return bookService.updateBookStatus(id, requestDto);
    }


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


    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }









}
