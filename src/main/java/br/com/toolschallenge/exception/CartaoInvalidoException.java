package br.com.toolschallenge.exception;

public class CartaoInvalidoException extends RuntimeException {
    public CartaoInvalidoException(String message) {
        super(message);
    }
}
