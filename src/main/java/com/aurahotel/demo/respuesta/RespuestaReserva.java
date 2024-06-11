/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.respuesta;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RespuestaReserva {
    private Long id;

    private LocalDate fechaEntrada;

    private LocalDate fechaSalida;

    private String nombreCompleto;

    private String email;

    private int numeroAdultos;

    private int numeroNinos;

    private int totalNumeroHuespedes;

    private String codigoConfirmacionReserva;

    private RespuestaHabitacion habitacion;

    public RespuestaReserva(Long id, LocalDate fechaEntrada, LocalDate fechaSalida, String codigoConfirmacionReserva) {
        this.id = id;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.codigoConfirmacionReserva = codigoConfirmacionReserva;
    }
}

