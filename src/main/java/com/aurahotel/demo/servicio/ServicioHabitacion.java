/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.servicio;

import com.aurahotel.demo.excepciones.ExcepcionInternaServidor;
import com.aurahotel.demo.excepciones.RecursoNoEncontradoException;
import com.aurahotel.demo.modelo.Habitacion;
import com.aurahotel.demo.repositorio.RepositorioHabitacion;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class ServicioHabitacion implements InterfazServicioHabitacion {
    private final RepositorioHabitacion repositorioHabitacion;

    @Override
    public Habitacion agregarNuevaHabitacion(MultipartFile archivo, String tipoHabitacion, BigDecimal precioHabitacion, String descripcion) throws SQLException, IOException {
        Habitacion habitacion = new Habitacion();
        habitacion.setTipoHabitacion(tipoHabitacion);
        habitacion.setPrecioHabitacion(precioHabitacion);
        habitacion.setDescripcion(descripcion);
        if (!archivo.isEmpty()) {
            byte[] fotoBytes = archivo.getBytes();
            Blob fotoBlob = new SerialBlob(fotoBytes);
            habitacion.setFoto(fotoBlob);
        }
        return repositorioHabitacion.save(habitacion);
    }


    @Override
    public List<String> obtenerTodosTiposHabitacion() {
        return repositorioHabitacion.encontrarTiposDeHabitacionDistintos();
    }

    @Override
    public List<Habitacion> obtenerTodasHabitaciones() {
        return repositorioHabitacion.findAll();
    }

    @Override
    public byte[] obtenerFotoHabitacionPorId(Long idHabitacion) throws SQLException {
        Optional<Habitacion> laHabitacion = repositorioHabitacion.findById(idHabitacion);

        if (laHabitacion.isEmpty()) {
            throw new RecursoNoEncontradoException("Lo siento, habitación no encontrada");
        }
        Blob fotoBlob = laHabitacion.get().getFoto();
        if (fotoBlob != null) {
            return fotoBlob.getBytes(1, (int) fotoBlob.length());
        }
        return null;
    }

    @Override
    public void eliminarHabitacion(Long idHabitacion) {
        Optional<Habitacion> laHabitacion = repositorioHabitacion.findById(idHabitacion);
        if (laHabitacion.isPresent()) {
            repositorioHabitacion.deleteById(idHabitacion);
        }
    }


    @Override
    public Habitacion actualizarHabitacion(Long idHabitacion, String tipoHabitacion, BigDecimal precioHabitacion, byte[] fotoBytes, String descripcion) {
        Habitacion habitacion = repositorioHabitacion.findById(idHabitacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lo siento, habitación no encontrada"));
        if (tipoHabitacion != null) {
            habitacion.setTipoHabitacion(tipoHabitacion);
        }
        if (precioHabitacion != null) {
            habitacion.setPrecioHabitacion(precioHabitacion);
        }
        if (descripcion != null) {
            habitacion.setDescripcion(descripcion);
        }
        if (fotoBytes != null && fotoBytes.length > 0) {
            try {
                habitacion.setFoto(new SerialBlob(fotoBytes));
            } catch (SQLException e) {
                throw new ExcepcionInternaServidor("Lo siento, algo salió mal");
            }
        }
        return repositorioHabitacion.save(habitacion);
    }

    @Override
    public Optional<Habitacion> obtenerHabitacionPorId(Long id) {
        return repositorioHabitacion.findById(id);
    }
    @Override
    public Optional<Habitacion> obtenerHabitacionPorTipo(String tipoHabitacion) {
        return repositorioHabitacion.findByTipoHabitacion(tipoHabitacion);
    }

    @Override
    public List<Habitacion> obtenerHabitacionesDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida, String tipoHabitacion) {
        return repositorioHabitacion.findHabitacionesDisponiblesPorFechaYTipo(fechaEntrada, fechaSalida, tipoHabitacion);
    }

}

