/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.modelo;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import org.apache.commons.lang3.RandomStringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipoHabitacion;
    private BigDecimal precioHabitacion;
    private boolean reservada = false;
    @Lob
    private Blob foto;
    private String descripcion; // Nueva descripción

    @OneToMany(mappedBy="habitacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HabitacionReservada> reservas;

    public Habitacion() {
        this.reservas = new ArrayList<>();
    }

    public void agregarReserva(HabitacionReservada reserva) {
        if (reservas == null) {
            reservas = new ArrayList<>();
        }
        reservas.add(reserva);
        reserva.setHabitacion(this);
        reservada = true;

        String codigoReserva = RandomStringUtils.randomNumeric(10);
        reserva.setCodigoConfirmacionReserva(codigoReserva);
    }
}
