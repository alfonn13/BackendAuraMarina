package com.aurahotel.demo.excepciones;

public class ExceptionRolYaExiste extends RuntimeException {
    public ExceptionRolYaExiste(String mensaje) {
        super(mensaje);
    }
}
