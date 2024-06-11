/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.controlador;

import com.aurahotel.demo.excepciones.ExcepcionRecursoNoEncontrado;
import com.aurahotel.demo.excepciones.ExcepcionSolicitudReservaInvalida;
import com.aurahotel.demo.modelo.Habitacion;
import com.aurahotel.demo.modelo.HabitacionReservada;
import com.aurahotel.demo.respuesta.RespuestaHabitacion;
import com.aurahotel.demo.respuesta.RespuestaReserva;
import com.aurahotel.demo.servicio.InterfazServicioHabitacion;
import com.aurahotel.demo.servicio.InterfazServicioReserva;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la gestión de reservas.
 * Proporciona endpoints para obtener, guardar, confirmar y cancelar reservas.
 */
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping("/reservas")
public class ControladorReservas {
    private final InterfazServicioReserva servicioReservas;
    private final InterfazServicioHabitacion servicioHabitaciones;

    /**
     * Endpoint para obtener todas las reservas.
     *
     * @return Una respuesta HTTP con una lista de todas las reservas.
     */
    @GetMapping("/todas-reservas")
    public ResponseEntity<List<RespuestaReserva>> obtenerTodasLasReservas(){
        List<HabitacionReservada> reservas = servicioReservas.obtenerTodasLasReservas();
        List<RespuestaReserva> respuestasReserva = new ArrayList<>();
        for (HabitacionReservada reserva : reservas){
            RespuestaReserva respuestaReserva = obtenerRespuestaReserva(reserva);
            respuestasReserva.add(respuestaReserva);
        }
        return ResponseEntity.ok(respuestasReserva);
    }

    /**
     * Endpoint para guardar una reserva.
     *
     * @param idHabitacion El ID de la habitación a reservar.
     * @param solicitudReserva La solicitud de reserva.
     * @return Una respuesta HTTP con el código de confirmación de la reserva.
     */
    @PostMapping("/habitacion/{idHabitacion}/reserva")
    public ResponseEntity<?> guardarReserva(@PathVariable Long idHabitacion,
                                            @RequestBody HabitacionReservada solicitudReserva){
        try{
            String codigoConfirmacion = servicioReservas.guardarReserva(idHabitacion, solicitudReserva);
            return ResponseEntity.ok(
                    "Habitación reservada exitosamente, tu código de confirmación de reserva es: "+codigoConfirmacion);

        }catch (ExcepcionSolicitudReservaInvalida e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Endpoint para obtener una reserva por su código de confirmación.
     *
     * @param codigoConfirmacion El código de confirmación de la reserva.
     * @return Una respuesta HTTP con los detalles de la reserva.
     */
    @GetMapping("/confirmacion/{codigoConfirmacion}")
    public ResponseEntity<?> obtenerReservaPorCodigoConfirmacion(@PathVariable String codigoConfirmacion){
        try{
            HabitacionReservada reserva = servicioReservas.obtenerReservaPorCodigoConfirmacion(codigoConfirmacion);
            RespuestaReserva respuestaReserva = obtenerRespuestaReserva(reserva);
            return ResponseEntity.ok(respuestaReserva);
        }catch (ExcepcionRecursoNoEncontrado ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Endpoint para obtener las reservas de un usuario por su correo electrónico.
     *
     * @param correoElectronico El correo electrónico del usuario.
     * @return Una respuesta HTTP con una lista de las reservas del usuario.
     */
    @GetMapping("/usuario/{correoElectronico}/reservas")
    public ResponseEntity<List<RespuestaReserva>> obtenerReservasPorCorreoElectronico(@PathVariable String correoElectronico) {
        List<HabitacionReservada> reservas = servicioReservas.obtenerReservasPorCorreoElectronico(correoElectronico);
        List<RespuestaReserva> respuestasReserva = new ArrayList<>();
        for (HabitacionReservada reserva : reservas) {
            RespuestaReserva respuestaReserva = obtenerRespuestaReserva(reserva);
            respuestasReserva.add(respuestaReserva);
        }
        return ResponseEntity.ok(respuestasReserva);
    }


    /**
     * Endpoint para cancelar una reserva.
     *
     * @param idReserva El ID de la reserva a cancelar.
     */
    @DeleteMapping("/reserva/{idReserva}/eliminar")
    public void cancelarReserva(@PathVariable Long idReserva){
        servicioReservas.cancelarReserva(idReserva);
    }

    private RespuestaReserva obtenerRespuestaReserva(HabitacionReservada reserva) {
        Habitacion laHabitacion = servicioHabitaciones.obtenerHabitacionPorId(reserva.getHabitacion().getId()).get();
        RespuestaHabitacion habitacion = new RespuestaHabitacion(
                laHabitacion.getId(),
                laHabitacion.getTipoHabitacion(),
                laHabitacion.getPrecioHabitacion(),
                laHabitacion.getDescripcion()
        );
        return new RespuestaReserva(
                reserva.getIdReserva(), reserva.getFechaEntrada(),
                reserva.getFechaSalida(),reserva.getNombreCompleto(),
                reserva.getEmail(),reserva.getNumeroAdultos(),
                reserva.getNumeroNinos(),reserva.getTotalNumeroHuespedes(),
                reserva.getCodigoConfirmacionReserva(), habitacion);
    }

    /**
     * Endpoint para confirmar una reserva.
     *
     * @param data Los datos de la reserva a confirmar.
     * @return Una respuesta HTTP con los detalles de la reserva confirmada.
     */
    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarReserva(@RequestBody Map<String, Object> data) throws StripeException {
        String sessionId = (String) data.get("sessionId");
        HabitacionReservada reserva = servicioReservas.confirmarReserva(sessionId);
        return ResponseEntity.ok(reserva);
    }
}

