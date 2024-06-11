/**
 * @author Alfonso Reviejo Valle
 * */

package com.aurahotel.demo.controlador;

import com.aurahotel.demo.modelo.HabitacionReservada;
import com.aurahotel.demo.respuesta.RespuestaPago;
import com.aurahotel.demo.servicio.InterfazServicioHabitacion;
import com.aurahotel.demo.servicio.InterfazServicioReserva;
import com.aurahotel.demo.servicio.ServicioPago;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Controlador para la gestión de pagos con Stripe.
 * Proporciona endpoints para crear una sesión de pago.
 */
@RestController
@RequestMapping("/api/payment")
public class ControladorStripe {

    @Autowired
    private ServicioPago servicioPago;
    @Autowired
    private InterfazServicioReserva servicioReservas;
    @Autowired
    private InterfazServicioHabitacion servicioHabitacion;

    /**
     * Endpoint para crear una sesión de pago con Stripe.
     *
     * @param data Los datos necesarios para crear la sesión de pago.
     * @return Una respuesta HTTP con los detalles del pago.
     * @throws StripeException Si ocurre un error al crear la sesión de pago.
     */
    @PostMapping("/create-checkout-session")
    public ResponseEntity<RespuestaPago> createCheckoutSession(@RequestBody Map<String, Object> data) throws StripeException {
        try {
            System.out.println("Datos recibidos: " + data);

            if (data.get("amount") == null || data.get("email") == null || data.get("idHabitacion") == null) {
                throw new IllegalArgumentException("Los datos de 'amount', 'email' y 'idHabitacion' son requeridos.");
            }

            int amount = Integer.parseInt(data.get("amount").toString());
            String email = data.get("email").toString();
            String nombre = data.get("nombre").toString();
            Long idHabitacion = Long.parseLong(data.get("idHabitacion").toString());
            LocalDate fechaEntrada = LocalDate.parse(data.get("fechaEntrada").toString());
            LocalDate fechaSalida = LocalDate.parse(data.get("fechaSalida").toString());
            int numeroAdultos = Integer.parseInt(data.get("numeroAdultos").toString());
            int numeroNinos = Integer.parseInt(data.get("numeroNinos").toString());

            System.out.println("Datos después de parseo: amount=" + amount + ", email=" + email + ", nombre=" + nombre +
                    ", idHabitacion=" + idHabitacion + ", fechaEntrada=" + fechaEntrada + ", fechaSalida=" + fechaSalida +
                    ", numeroAdultos=" + numeroAdultos + ", numeroNinos=" + numeroNinos);

            RespuestaPago respuestaPago = servicioPago.crearMetodoPago(amount);


            String codigoConfirmacion = servicioReservas.generarCodigoConfirmacion();


            HabitacionReservada solicitudReserva = new HabitacionReservada();
            solicitudReserva.setEmail(email);
            solicitudReserva.setNombreCompleto(nombre);
            solicitudReserva.setFechaEntrada(fechaEntrada);
            solicitudReserva.setFechaSalida(fechaSalida);
            solicitudReserva.setNumeroAdultos(numeroAdultos);
            solicitudReserva.setNumeroNinos(numeroNinos);
            solicitudReserva.setCodigoConfirmacionReserva(codigoConfirmacion);
            solicitudReserva.setPagoConfirmado(false);
            solicitudReserva.setHabitacion(servicioHabitacion.obtenerHabitacionPorId(idHabitacion).orElseThrow(() -> new RuntimeException("Habitación no encontrada")));

            System.out.println("Datos de la reserva antes de guardar: " + solicitudReserva);


            servicioReservas.guardarReserva(idHabitacion, solicitudReserva);

            return ResponseEntity.ok(respuestaPago);
        } catch (Exception e) {
            RespuestaPago respuestaError = new RespuestaPago(null, "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaError);
        }
    }

}
