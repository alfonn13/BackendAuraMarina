/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.repositorio;

import com.aurahotel.demo.modelo.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioHabitacion extends JpaRepository<Habitacion, Long> {
    Optional<Habitacion> findByTipoHabitacion(String tipoHabitacion);
    @Query("SELECT DISTINCT r.tipoHabitacion FROM Habitacion r")
    List<String> encontrarTiposDeHabitacionDistintos();

    @Query("SELECT r FROM Habitacion r " +
            "WHERE r.tipoHabitacion LIKE %:tipoHabitacion% " +
            "AND r.id NOT IN (" +
            "  SELECT br.habitacion.id FROM HabitacionReservada br " +
            "  WHERE ((br.fechaEntrada <= :fechaSalida) AND (br.fechaSalida >= :fechaEntrada))" +
            ")")


    List<Habitacion> findHabitacionesDisponiblesPorFechaYTipo(LocalDate fechaEntrada, LocalDate fechaSalida, String tipoHabitacion);
}

