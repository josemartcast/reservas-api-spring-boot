package com.mycompany.excepciones;

public class ReservaNoEncontradaException extends RuntimeException {

    public ReservaNoEncontradaException(String message) {
        super(message);
    }
}
