package br.com.toolschallenge.config;

import br.com.toolschallenge.exception.CartaoInvalidoException;
import br.com.toolschallenge.exception.DadosInvalidosException;
import br.com.toolschallenge.exception.ExtornoIndisponivelException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<?> handleDadosInvalidos(DadosInvalidosException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(CartaoInvalidoException.class)
    public ResponseEntity<?> handleCartao(CartaoInvalidoException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ExtornoIndisponivelException.class)
    public ResponseEntity<?> handleExtornoIndisponivel(ExtornoIndisponivelException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}