/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.repositorio;

import com.aurahotel.demo.modelo.HabitacionReservada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepositorioReservas extends JpaRepository<HabitacionReservada, Long> {

    Optional<HabitacionReservada> findByCodigoConfirmacionReserva(String codigoConfirmacionReservas);

    List<HabitacionReservada> findByHabitacion_Id(Long idHabitacion);

    List<HabitacionReservada> findByEmail(String correoElectronico);

    Optional<HabitacionReservada> findByStripeSessionId(String stripeSessionId);

}
