/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.respuesta;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Base64;

@Data
@NoArgsConstructor
public class RespuestaHabitacion {

    private Long id;
    private String tipoHabitacion;
    private BigDecimal precioHabitacion;
    private boolean reservada = false;
    private String foto;
    private String descripcion;

    public RespuestaHabitacion(Long id, String tipoHabitacion, BigDecimal precioHabitacion, String descripcion, String foto) {
        this.id = id;
        this.tipoHabitacion = tipoHabitacion;
        this.precioHabitacion = precioHabitacion;
        this.descripcion = descripcion;
        this.foto = foto;
    }

    public RespuestaHabitacion(Long id, String tipoHabitacion, BigDecimal precioHabitacion, String descripcion) {
        this.id = id;
        this.tipoHabitacion = tipoHabitacion;
        this.precioHabitacion = precioHabitacion;
        this.descripcion = descripcion;
    }

    public RespuestaHabitacion(Long id, String tipoHabitacion, BigDecimal precioHabitacion, boolean reservada, byte[] fotoBytes) {
        this.id = id;
        this.tipoHabitacion = tipoHabitacion;
        this.precioHabitacion = precioHabitacion;
        this.reservada = reservada;
        this.foto = Base64.getEncoder().encodeToString(fotoBytes);
    }
}
