package com.aurahotel.demo.excepciones;

    public class ExcepcionNoEncuentraUsuario extends RuntimeException {
    public ExcepcionNoEncuentraUsuario(String mensaje) {
        super(mensaje);
    }
}
