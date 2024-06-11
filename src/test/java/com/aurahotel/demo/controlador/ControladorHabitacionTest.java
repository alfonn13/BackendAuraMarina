package com.aurahotel.demo.controlador;

import com.aurahotel.demo.excepciones.ExcepcionRecuperarFoto;
import com.aurahotel.demo.excepciones.ExcepcionRecursoNoEncontrado;
import com.aurahotel.demo.modelo.Habitacion;
import com.aurahotel.demo.respuesta.RespuestaHabitacion;
import com.aurahotel.demo.servicio.InterfazServicioHabitacion;
import com.aurahotel.demo.servicio.ServicioReserva;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorHabitacion.class)
public class ControladorHabitacionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterfazServicioHabitacion servicioHabitacion;

    @MockBean
    private ServicioReserva servicioReserva;

    private Habitacion habitacion;

    @BeforeEach
    public void setUp() {
        habitacion = new Habitacion();
        habitacion.setId(1L);
        habitacion.setTipoHabitacion("Suite");
        habitacion.setPrecioHabitacion(new BigDecimal("150.00"));
        habitacion.setDescripcion("Una habitación muy cómoda");

        // Crear un Blob ficticio para la foto
        Blob fotoBlob = Mockito.mock(Blob.class);
        habitacion.setFoto(fotoBlob);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAgregarNuevaHabitacion() throws Exception {
        MockMultipartFile foto = new MockMultipartFile("foto", "foto.jpg", "image/jpeg", "foto".getBytes());

        Mockito.when(servicioHabitacion.agregarNuevaHabitacion(any(), any(), any(), any()))
                .thenReturn(habitacion);

        mockMvc.perform(multipart("/habitaciones/agregar/nueva-habitacion")
                        .file(foto)
                        .param("tipoHabitacion", "Suite")
                        .param("precioHabitacion", "150.00")
                        .param("descripcion", "Una habitación muy cómoda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoHabitacion").value("Suite"))
                .andExpect(jsonPath("$.precioHabitacion").value(150.00))
                .andExpect(jsonPath("$.descripcion").value("Una habitación muy cómoda"));
    }

    @Test
    public void testObtenerTiposHabitacion() throws Exception {
        List<String> tiposHabitacion = List.of("Suite", "Doble", "Individual");

        Mockito.when(servicioHabitacion.obtenerTodosTiposHabitacion())
                .thenReturn(tiposHabitacion);

        mockMvc.perform(get("/habitaciones/tipos-habitacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Suite"))
                .andExpect(jsonPath("$[1]").value("Doble"))
                .andExpect(jsonPath("$[2]").value("Individual"));
    }

    @Test
    public void testObtenerTodasHabitaciones() throws Exception, SQLException, ExcepcionRecuperarFoto {
        List<Habitacion> habitaciones = new ArrayList<>();
        habitaciones.add(habitacion);

        Mockito.when(servicioHabitacion.obtenerTodasHabitaciones())
                .thenReturn(habitaciones);

        mockMvc.perform(get("/habitaciones/todas-habitaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoHabitacion").value("Suite"))
                .andExpect(jsonPath("$[0].precioHabitacion").value(150.00))
                .andExpect(jsonPath("$[0].descripcion").value("Una habitación muy cómoda"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testEliminarHabitacion() throws Exception {
        mockMvc.perform(delete("/habitaciones/eliminar/habitacion/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(servicioHabitacion, Mockito.times(1)).eliminarHabitacion(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testActualizarHabitacion() throws Exception {
        MockMultipartFile foto = new MockMultipartFile("foto", "foto.jpg", "image/jpeg", "foto".getBytes());
        byte[] fotoBytes = foto.getBytes();
        Blob fotoBlob = new javax.sql.rowset.serial.SerialBlob(fotoBytes);
        habitacion.setFoto(fotoBlob);

        Mockito.when(servicioHabitacion.actualizarHabitacion(anyLong(), any(), any(), any(), any()))
                .thenReturn(habitacion);

        mockMvc.perform(multipart("/habitaciones/actualizar/1")
                        .file(foto)
                        .param("tipoHabitacion", "Suite")
                        .param("precioHabitacion", "150.00")
                        .param("descripcion", "Una habitación muy cómoda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoHabitacion").value("Suite"))
                .andExpect(jsonPath("$.precioHabitacion").value(150.00))
                .andExpect(jsonPath("$.descripcion").value("Una habitación muy cómoda"));
    }

    @Test
    public void testObtenerHabitacionPorId() throws Exception, SQLException {
        Mockito.when(servicioHabitacion.obtenerHabitacionPorId(anyLong()))
                .thenReturn(Optional.of(habitacion));

        mockMvc.perform(get("/habitaciones/habitacion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoHabitacion").value("Suite"))
                .andExpect(jsonPath("$.precioHabitacion").value(150.00))
                .andExpect(jsonPath("$.descripcion").value("Una habitación muy cómoda"));
    }

    @Test
    public void testObtenerHabitacionesDisponibles() throws Exception, SQLException, ExcepcionRecuperarFoto {
        List<Habitacion> habitacionesDisponibles = new ArrayList<>();
        habitacionesDisponibles.add(habitacion);

        Mockito.when(servicioHabitacion.obtenerHabitacionesDisponibles(any(), any(), any()))
                .thenReturn(habitacionesDisponibles);

        mockMvc.perform(get("/habitaciones/habitaciones-disponibles")
                        .param("fechaEntrada", "2024-06-10")
                        .param("fechaSalida", "2024-06-15")
                        .param("tipoHabitacion", "Suite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoHabitacion").value("Suite"))
                .andExpect(jsonPath("$[0].precioHabitacion").value(150.00))
                .andExpect(jsonPath("$[0].descripcion").value("Una habitación muy cómoda"));
    }
}
