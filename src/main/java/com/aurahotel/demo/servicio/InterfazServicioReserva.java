/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.servicio;

import com.aurahotel.demo.modelo.Habitacion;
import com.aurahotel.demo.modelo.HabitacionReservada;
import com.stripe.exception.StripeException;

import java.util.List;
import java.util.Optional;

public interface InterfazServicioReserva {

    Optional<Habitacion> obtenerHabitacionPorId(Long idHabitacion);

    void cancelarReserva(Long idReserva);

    List<HabitacionReservada> obtenerTodasLasReservasPorIdHabitacion(Long idHabitacion);

    void guardarReservaTemporal(HabitacionReservada solicitudReserva);

    String guardarReserva(Long idHabitacion, HabitacionReservada solicitudReserva);

    HabitacionReservada obtenerReservaPorCodigoConfirmacion(String codigoConfirmacion);

    List<HabitacionReservada> obtenerTodasLasReservas();

    List<HabitacionReservada> obtenerReservasPorCorreoElectronico(String correoElectronico);

    HabitacionReservada confirmarReserva(String sessionId) throws StripeException;

    String generarCodigoConfirmacion();
}



