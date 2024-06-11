package com.aurahotel.demo.controlador;

import com.aurahotel.demo.modelo.Habitacion;
import com.aurahotel.demo.modelo.HabitacionReservada;
import com.aurahotel.demo.respuesta.RespuestaPago;
import com.aurahotel.demo.servicio.InterfazServicioHabitacion;
import com.aurahotel.demo.servicio.InterfazServicioReserva;
import com.aurahotel.demo.servicio.ServicioPago;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorStripe.class)
public class ControladorStripeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioPago servicioPago;

    @MockBean
    private InterfazServicioReserva servicioReservas;

    @MockBean
    private InterfazServicioHabitacion servicioHabitacion;

    private Habitacion habitacion;
    private HabitacionReservada habitacionReservada;
    private RespuestaPago respuestaPago;

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

        respuestaPago = new RespuestaPago("sess_123", "Creación exitosa");
    }

    @Test
    public void testCreateCheckoutSession() throws Exception {
        Mockito.when(servicioPago.crearMetodoPago(anyInt())).thenReturn(respuestaPago);
        Mockito.when(servicioReservas.generarCodigoConfirmacion()).thenReturn("CONF123");
        Mockito.when(servicioHabitacion.obtenerHabitacionPorId(anyLong())).thenReturn(Optional.of(habitacion));

        Map<String, Object> data = new HashMap<>();
        data.put("amount", 15000);
        data.put("email", "johndoe@example.com");
        data.put("nombre", "John Doe");
        data.put("idHabitacion", 1L);
        data.put("fechaEntrada", LocalDate.now().toString());
        data.put("fechaSalida", LocalDate.now().plusDays(1).toString());
        data.put("numeroAdultos", 2);
        data.put("numeroNinos", 0);

        mockMvc.perform(post("/api/payment/create-checkout-session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":15000,\"email\":\"johndoe@example.com\",\"nombre\":\"John Doe\",\"idHabitacion\":1,\"fechaEntrada\":\"" + LocalDate.now() + "\",\"fechaSalida\":\"" + LocalDate.now().plusDays(1) + "\",\"numeroAdultos\":2,\"numeroNinos\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Creación exitosa"));
    }

    @Test
    public void testCreateCheckoutSessionError() throws Exception {
        Mockito.when(servicioPago.crearMetodoPago(anyInt()));

        mockMvc.perform(post("/api/payment/create-checkout-session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":15000,\"email\":\"johndoe@example.com\",\"nombre\":\"John Doe\",\"idHabitacion\":1,\"fechaEntrada\":\"" + LocalDate.now() + "\",\"fechaSalida\":\"" + LocalDate.now().plusDays(1) + "\",\"numeroAdultos\":2,\"numeroNinos\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Stripe error"));
    }
}
