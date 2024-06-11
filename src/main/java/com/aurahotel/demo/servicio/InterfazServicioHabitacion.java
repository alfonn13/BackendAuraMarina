/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.servicio;

import com.aurahotel.demo.modelo.Habitacion;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InterfazServicioHabitacion {


    Habitacion agregarNuevaHabitacion(MultipartFile foto, String tipoHabitacion, BigDecimal precioHabitacion, String descripcion)throws SQLException, IOException;

    List<String> obtenerTodosTiposHabitacion();


    List<Habitacion> obtenerTodasHabitaciones();

    byte[] obtenerFotoHabitacionPorId(Long id) throws SQLException;

    void eliminarHabitacion(Long idHabitacion);

    Habitacion actualizarHabitacion(Long idHabitacion, String tipoHabitacion, BigDecimal precioHabitacion, byte[] fotoBytes, String descripcion);

    Optional<Habitacion> obtenerHabitacionPorId(Long id);

    Optional<Habitacion> obtenerHabitacionPorTipo(String tipoHabitacion);

    List<Habitacion> obtenerHabitacionesDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida, String tipoHabitacion);

}
