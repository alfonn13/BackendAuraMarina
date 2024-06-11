package com.aurahotel.demo.excepciones;

public class ExceptionUsuarioYaExiste extends RuntimeException {
    public ExceptionUsuarioYaExiste(String mensaje) {
        super(mensaje);
    }
}
