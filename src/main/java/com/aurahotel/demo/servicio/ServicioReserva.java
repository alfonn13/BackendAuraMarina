/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.servicio;

import com.aurahotel.demo.excepciones.ExcepcionRecursoNoEncontrado;
import com.aurahotel.demo.excepciones.ExcepcionSolicitudReservaInvalida;
import com.aurahotel.demo.modelo.Habitacion;
import com.aurahotel.demo.modelo.HabitacionReservada;
import com.aurahotel.demo.repositorio.RepositorioReservas;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicioReserva implements InterfazServicioReserva {

    private final RepositorioReservas repositorioReservas;
    private final ServicioHabitacion servicioHabitaciones;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Override
    public List<HabitacionReservada> obtenerTodasLasReservas() {
        return repositorioReservas.findAll();
    }

    @Override
    public List<HabitacionReservada> obtenerReservasPorCorreoElectronico(String correoElectronico) {
        return repositorioReservas.findByEmail(correoElectronico);
    }
    @Override
    public void guardarReservaTemporal(HabitacionReservada solicitudReserva) {
        repositorioReservas.save(solicitudReserva);
    }
    @Override
    public HabitacionReservada confirmarReserva(String sessionId) throws StripeException {
        HabitacionReservada reserva = repositorioReservas.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada para la sesión: " + sessionId));


        reserva.setPagoConfirmado(true);
        return repositorioReservas.save(reserva);
    }

    @Override
    public Optional<Habitacion> obtenerHabitacionPorId(Long idHabitacion) {
        return Optional.empty();
    }

    @Override
    public void cancelarReserva(Long idReserva) {
        repositorioReservas.deleteById(idReserva);
    }

    @Override
    public List<HabitacionReservada> obtenerTodasLasReservasPorIdHabitacion(Long idHabitacion) {
        return repositorioReservas.findByHabitacion_Id(idHabitacion);
    }

    @Override
    public String guardarReserva(Long idHabitacion, HabitacionReservada solicitudReserva) {
        if (solicitudReserva.getFechaSalida().isBefore(solicitudReserva.getFechaEntrada())) {
            throw new ExcepcionSolicitudReservaInvalida("La fecha de entrada debe ser anterior a la fecha de salida");
        }
        Habitacion habitacion = servicioHabitaciones.obtenerHabitacionPorId(idHabitacion).get();
        List<HabitacionReservada> reservasExistentes = habitacion.getReservas();
        boolean habitacionDisponible = habitacionDisponible(solicitudReserva, reservasExistentes);
        if (habitacionDisponible) {
            habitacion.agregarReserva(solicitudReserva);
            repositorioReservas.save(solicitudReserva);
        } else {
            throw new ExcepcionSolicitudReservaInvalida("Lo sentimos, esta habitación no está disponible para las fechas seleccionadas;");
        }
        return solicitudReserva.getCodigoConfirmacionReserva();
    }

    @Override
    public HabitacionReservada obtenerReservaPorCodigoConfirmacion(String codigoConfirmacionReservas) {
        Optional<HabitacionReservada> reservaOpcional = repositorioReservas.findByCodigoConfirmacionReserva(codigoConfirmacionReservas);
        return reservaOpcional.orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Reserva no encontrada para el código de confirmación: " + codigoConfirmacionReservas));
    }
    @Override
    public String generarCodigoConfirmacion() {
        // Implementar la lógica de generación del código de confirmación según lo requiera la aplicación
        return String.valueOf(System.currentTimeMillis());
    }

    private boolean habitacionDisponible(HabitacionReservada solicitudReserva, List<HabitacionReservada> reservasExistentes) {
        return reservasExistentes.stream()
                .noneMatch(reservaExistente ->
                        solicitudReserva.getFechaEntrada().equals(reservaExistente.getFechaEntrada())
                                || solicitudReserva.getFechaSalida().isBefore(reservaExistente.getFechaSalida())
                                || (solicitudReserva.getFechaEntrada().isAfter(reservaExistente.getFechaEntrada())
                                && solicitudReserva.getFechaEntrada().isBefore(reservaExistente.getFechaSalida()))
                                || (solicitudReserva.getFechaEntrada().isBefore(reservaExistente.getFechaEntrada())
                                && solicitudReserva.getFechaSalida().equals(reservaExistente.getFechaSalida()))
                                || (solicitudReserva.getFechaEntrada().isBefore(reservaExistente.getFechaEntrada())
                                && solicitudReserva.getFechaSalida().isAfter(reservaExistente.getFechaSalida()))
                                || (solicitudReserva.getFechaEntrada().equals(reservaExistente.getFechaSalida())
                                && solicitudReserva.getFechaSalida().equals(reservaExistente.getFechaEntrada()))
                                || (solicitudReserva.getFechaEntrada().equals(reservaExistente.getFechaSalida())
                                && solicitudReserva.getFechaSalida().equals(solicitudReserva.getFechaEntrada()))
                );
    }
}
