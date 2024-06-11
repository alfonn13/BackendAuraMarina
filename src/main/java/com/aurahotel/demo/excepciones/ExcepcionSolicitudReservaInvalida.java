package com.aurahotel.demo.excepciones;

public class ExcepcionSolicitudReservaInvalida extends RuntimeException{
    public ExcepcionSolicitudReservaInvalida(String mensaje){
        super(mensaje);
    }
}
