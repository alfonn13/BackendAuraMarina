/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.controlador;

import com.aurahotel.demo.excepciones.ExcepcionRecuperarFoto;
import com.aurahotel.demo.excepciones.ExcepcionRecursoNoEncontrado;
import com.aurahotel.demo.modelo.Habitacion;
import com.aurahotel.demo.modelo.HabitacionReservada;
import com.aurahotel.demo.respuesta.RespuestaHabitacion;
import com.aurahotel.demo.servicio.InterfazServicioHabitacion;
import com.aurahotel.demo.servicio.ServicioReserva;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de habitaciones.
 * Proporciona endpoints para agregar, actualizar, eliminar y obtener habitaciones.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/habitaciones")
@CrossOrigin(origins = "http://localhost:5173")
public class ControladorHabitacion {

    private final InterfazServicioHabitacion servicioHabitacion;
    private final ServicioReserva servicioReserva;

    /**
     * Endpoint para agregar una nueva habitación.
     *
     * @param foto La foto de la habitación.
     * @param tipoHabitacion El tipo de la habitación.
     * @param precioHabitacion El precio de la habitación.
     * @param descripcion La descripción de la habitación.
     * @return Una respuesta HTTP con los detalles de la habitación agregada.
     */
    @PostMapping("/agregar/nueva-habitacion")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RespuestaHabitacion> agregarNuevaHabitacion(@RequestParam("foto") MultipartFile foto,
                                                                      @RequestParam("tipoHabitacion") String tipoHabitacion,
                                                                      @RequestParam("precioHabitacion") BigDecimal precioHabitacion,
                                                                      @RequestParam("descripcion") String descripcion) throws SQLException, IOException {
        System.out.println("Descripción recibida: " + descripcion);
        Habitacion habitacionGuardada = servicioHabitacion.agregarNuevaHabitacion(foto, tipoHabitacion, precioHabitacion, descripcion);
        String fotoBase64 = habitacionGuardada.getFoto() != null ? Base64.encodeBase64String(habitacionGuardada.getFoto().getBytes(1, (int) habitacionGuardada.getFoto().length())) : null;
        RespuestaHabitacion respuesta = new RespuestaHabitacion(habitacionGuardada.getId(), habitacionGuardada.getTipoHabitacion(), habitacionGuardada.getPrecioHabitacion(), habitacionGuardada.getDescripcion(), fotoBase64);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Endpoint para obtener todos los tipos de habitaciones.
     *
     * @return Una lista con todos los tipos de habitaciones.
     */
    @GetMapping("/tipos-habitacion")
    public List<String> obtenerTiposHabitacion() {
        return servicioHabitacion.obtenerTodosTiposHabitacion();
    }

    /**
     * Endpoint para obtener todas las habitaciones.
     *
     * @return Una respuesta HTTP con una lista de todas las habitaciones.
     */
    @GetMapping("/todas-habitaciones")
    public ResponseEntity<List<RespuestaHabitacion>> obtenerTodasHabitaciones() throws SQLException, ExcepcionRecuperarFoto {
        List<Habitacion> habitaciones = servicioHabitacion.obtenerTodasHabitaciones();
        List<RespuestaHabitacion> respuestasHabitacion = new ArrayList<>();

        for (Habitacion habitacion : habitaciones) {
            RespuestaHabitacion respuestaHabitacion = obtenerRespuestaHabitacion(habitacion);
            respuestasHabitacion.add(respuestaHabitacion);
        }
        return ResponseEntity.ok(respuestasHabitacion);
    }

    /**
     * Endpoint para eliminar una habitación.
     *
     * @param idHabitacion El ID de la habitación a eliminar.
     * @return Una respuesta HTTP indicando el resultado de la operación.
     */
    @DeleteMapping("/eliminar/habitacion/{idHabitacion}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminarHabitacion(@PathVariable Long idHabitacion) {
        servicioHabitacion.eliminarHabitacion(idHabitacion);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint para actualizar una habitación.
     *
     * @param idHabitacion El ID de la habitación a actualizar.
     * @param tipoHabitacion El nuevo tipo de la habitación.
     * @param precioHabitacion El nuevo precio de la habitación.
     * @param foto La nueva foto de la habitación.
     * @param descripcion La nueva descripción de la habitación.
     * @return Una respuesta HTTP con los detalles de la habitación actualizada.
     */
    @PutMapping("/actualizar/{idHabitacion}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RespuestaHabitacion> actualizarHabitacion(@PathVariable Long idHabitacion,
                                                                    @RequestParam(required = false) String tipoHabitacion,
                                                                    @RequestParam(required = false) BigDecimal precioHabitacion,
                                                                    @RequestParam(required = false) MultipartFile foto,
                                                                    @RequestParam(required = false) String descripcion) throws IOException, SQLException, ExcepcionRecuperarFoto {
        byte[] fotoBytes = foto != null && !foto.isEmpty() ? foto.getBytes() : servicioHabitacion.obtenerFotoHabitacionPorId(idHabitacion);

        Blob fotoBlob = fotoBytes != null && fotoBytes.length > 0 ? new SerialBlob(fotoBytes) : null;

        Habitacion habitacionActualizada = servicioHabitacion.actualizarHabitacion(idHabitacion, tipoHabitacion, precioHabitacion, fotoBytes, descripcion);
        habitacionActualizada.setFoto(fotoBlob);
        RespuestaHabitacion respuesta = obtenerRespuestaHabitacion(habitacionActualizada);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Endpoint para obtener una habitación por su ID.
     *
     * @param idHabitacion El ID de la habitación a obtener.
     * @return Una respuesta HTTP con los detalles de la habitación solicitada.
     */
    @GetMapping("/habitacion/{idHabitacion}")
    public ResponseEntity<Optional<RespuestaHabitacion>> obtenerHabitacionPorId(@PathVariable Long idHabitacion) throws SQLException {
        Optional<Habitacion> laHabitacion = servicioHabitacion.obtenerHabitacionPorId(idHabitacion);

        return laHabitacion.map(habitacion -> {
            RespuestaHabitacion respuestaHabitacion = null;
            try {
                respuestaHabitacion = obtenerRespuestaHabitacion(habitacion);
            } catch (ExcepcionRecuperarFoto e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.ok(Optional.of(respuestaHabitacion));
        }).orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Lo siento, Habitación no encontrada"));
    }

    private RespuestaHabitacion obtenerRespuestaHabitacion(Habitacion habitacion) throws ExcepcionRecuperarFoto {
        byte[] fotoBytes = null;
        Blob fotoBlob = habitacion.getFoto();
        if (fotoBlob != null) {
            try {
                fotoBytes = fotoBlob.getBytes(1, (int) fotoBlob.length());
            } catch (SQLException e) {
                throw new ExcepcionRecuperarFoto("Error al recuperar la foto");
            }
        }
        String fotoBase64 = fotoBytes != null ? Base64.encodeBase64String(fotoBytes) : null;
        return new RespuestaHabitacion(habitacion.getId(), habitacion.getTipoHabitacion(), habitacion.getPrecioHabitacion(), habitacion.getDescripcion(), fotoBase64);
    }

    private List<HabitacionReservada> obtenerTodasReservasPorIdHabitacion(Long idHabitacion) {
        return servicioReserva.obtenerTodasLasReservasPorIdHabitacion(idHabitacion);
    }

    /**
     * Endpoint para obtener las habitaciones disponibles.
     *
     * @param fechaEntrada La fecha de entrada.
     * @param fechaSalida La fecha de salida.
     * @param tipoHabitacion El tipo de habitación.
     * @return Una respuesta HTTP con una lista de las habitaciones disponibles.
     */
    @GetMapping("/habitaciones-disponibles")
    public ResponseEntity<List<RespuestaHabitacion>> obtenerHabitacionesDisponibles(
            @RequestParam("fechaEntrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam("fechaSalida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam("tipoHabitacion") String tipoHabitacion) throws SQLException, ExcepcionRecuperarFoto {
        List<Habitacion> habitacionesDisponibles = servicioHabitacion.obtenerHabitacionesDisponibles(fechaEntrada, fechaSalida, tipoHabitacion);
        List<RespuestaHabitacion> respuestasHabitacion = new ArrayList<>();
        for (Habitacion h : habitacionesDisponibles) {
            RespuestaHabitacion respuestaHabitacion = obtenerRespuestaHabitacion(h);
            respuestasHabitacion.add(respuestaHabitacion);
        }
        return ResponseEntity.ok(respuestasHabitacion);
    }
}
