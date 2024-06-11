/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HabitacionReservada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    @Column(name = "fecha_entrada")
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @Column(name = "email")
    private String email;

    @Column(name = "adultos")
    private int numeroAdultos = 0;

    @Column(name = "ninos")
    private int numeroNinos = 0;

    @Column(name = "total_huespedes")
    private int totalNumeroHuespedes = 0;

    @Column(name = "codigo_confirmacion")
    private String codigoConfirmacionReserva;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Column(name = "pago_confirmado")
    private Boolean pagoConfirmado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habitacion_id")
    private Habitacion habitacion;

    public void calcularTotalHuespedes() {
        this.totalNumeroHuespedes = this.numeroAdultos + this.numeroNinos;
    }

    public void setNumeroAdultos(int numeroAdultos) {
        this.numeroAdultos = numeroAdultos;
        calcularTotalHuespedes();
    }

    public void setNumeroNinos(int numeroNinos) {
        this.numeroNinos = numeroNinos;
        calcularTotalHuespedes();
    }

    public void setCodigoConfirmacionReserva(String codigoConfirmacionReserva) {
        this.codigoConfirmacionReserva = codigoConfirmacionReserva;
    }

    public void setSessionId(String sessionId) {
        this.stripeSessionId = sessionId;
    }
}
