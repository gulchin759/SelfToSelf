package org.example.shelftoshelf.ExceptionManager;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(String message) {
        super(message);
    }
}
