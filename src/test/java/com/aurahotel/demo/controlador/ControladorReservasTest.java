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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorReservas.class)
public class ControladorReservasTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterfazServicioReserva servicioReservas;

    @MockBean
    private InterfazServicioHabitacion servicioHabitaciones;

    private Habitacion habitacion;
    private HabitacionReservada habitacionReservada;

    @BeforeEach
    public void setUp() {
        habitacion = new Habitacion();
        habitacion.setId(1L);
        habitacion.setTipoHabitacion("Suite");
        habitacion.setPrecioHabitacion(new BigDecimal("150.00"));
        habitacion.setDescripcion("Una habitación muy cómoda");

        habitacionReservada = new HabitacionReservada();
        habitacionReservada.setIdReserva(1L);
        habitacionReservada.setFechaEntrada(LocalDate.now());
        habitacionReservada.setFechaSalida(LocalDate.now().plusDays(1));
        habitacionReservada.setNombreCompleto("John Doe");
        habitacionReservada.setEmail("johndoe@example.com");
        habitacionReservada.setNumeroAdultos(2);
        habitacionReservada.setNumeroNinos(0);
        habitacionReservada.setTotalNumeroHuespedes(2);
        habitacionReservada.setCodigoConfirmacionReserva("CONF123");
        habitacionReservada.setHabitacion(habitacion);
    }

    @Test
    public void testObtenerTodasLasReservas() throws Exception {
        List<HabitacionReservada> reservas = List.of(habitacionReservada);

        Mockito.when(servicioReservas.obtenerTodasLasReservas()).thenReturn(reservas);

        mockMvc.perform(get("/reservas/todas-reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCompleto").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("johndoe@example.com"))
                .andExpect(jsonPath("$[0].codigoConfirmacionReserva").value("CONF123"));
    }

    @Test
    public void testGuardarReserva() throws Exception {
        Mockito.when(servicioReservas.guardarReserva(anyLong(), any(HabitacionReservada.class))).thenReturn("CONF123");

        mockMvc.perform(post("/reservas/habitacion/1/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCompleto\":\"John Doe\",\"email\":\"johndoe@example.com\",\"numeroAdultos\":2,\"numeroNinos\":0,\"totalNumeroHuespedes\":2,\"codigoConfirmacionReserva\":\"CONF123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Habitación reservada exitosamente, tu código de confirmación de reserva es: CONF123"));
    }

    @Test
    public void testGuardarReservaInvalida() throws Exception {
        Mockito.when(servicioReservas.guardarReserva(anyLong(), any(HabitacionReservada.class))).thenThrow(new ExcepcionSolicitudReservaInvalida("Reserva inválida"));

        mockMvc.perform(post("/reservas/habitacion/1/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCompleto\":\"John Doe\",\"email\":\"johndoe@example.com\",\"numeroAdultos\":2,\"numeroNinos\":0,\"totalNumeroHuespedes\":2,\"codigoConfirmacionReserva\":\"CONF123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Reserva inválida"));
    }

    @Test
    public void testObtenerReservaPorCodigoConfirmacion() throws Exception {
        Mockito.when(servicioReservas.obtenerReservaPorCodigoConfirmacion(anyString())).thenReturn(habitacionReservada);

        mockMvc.perform(get("/reservas/confirmacion/CONF123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("John Doe"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"))
                .andExpect(jsonPath("$.codigoConfirmacionReserva").value("CONF123"));
    }

    @Test
    public void testObtenerReservaPorCodigoConfirmacionNoEncontrada() throws Exception {
        Mockito.when(servicioReservas.obtenerReservaPorCodigoConfirmacion(anyString())).thenThrow(new ExcepcionRecursoNoEncontrado("Reserva no encontrada"));

        mockMvc.perform(get("/reservas/confirmacion/CONF123"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Reserva no encontrada"));
    }

    @Test
    public void testObtenerReservasPorCorreoElectronico() throws Exception {
        List<HabitacionReservada> reservas = List.of(habitacionReservada);

        Mockito.when(servicioReservas.obtenerReservasPorCorreoElectronico(anyString())).thenReturn(reservas);

        mockMvc.perform(get("/reservas/usuario/johndoe@example.com/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCompleto").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("johndoe@example.com"))
                .andExpect(jsonPath("$[0].codigoConfirmacionReserva").value("CONF123"));
    }

    @Test
    public void testCancelarReserva() throws Exception {
        mockMvc.perform(delete("/reservas/reserva/1/eliminar"))
                .andExpect(status().isNoContent());

        Mockito.verify(servicioReservas, Mockito.times(1)).cancelarReserva(1L);
    }

    @Test
    public void testConfirmarReserva() throws Exception {
        Mockito.when(servicioReservas.confirmarReserva(anyString())).thenReturn(habitacionReservada);

        Map<String, Object> data = new HashMap<>();
        data.put("sessionId", "sess_123");

        mockMvc.perform(post("/reservas/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"sess_123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("John Doe"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"))
                .andExpect(jsonPath("$.codigoConfirmacionReserva").value("CONF123"));
    }
}
