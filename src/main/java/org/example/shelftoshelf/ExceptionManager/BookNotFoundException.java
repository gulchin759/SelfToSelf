package org.example.shelftoshelf.ExceptionManager;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
