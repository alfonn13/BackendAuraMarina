package com.aurahotel.demo.excepciones;

public class ExcepcionInternaServidor extends RuntimeException{
    public ExcepcionInternaServidor(String mensaje) {
        super(mensaje);
    }
}
