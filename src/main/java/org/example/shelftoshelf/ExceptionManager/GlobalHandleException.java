package org.example.shelftoshelf.ExceptionManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalHandleException {



    @ExceptionHandler({BookNotFoundException.class, CustomerNotFoundException.class,StoreNotFoundException.class})
    public ResponseEntity<String> handleBookNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
