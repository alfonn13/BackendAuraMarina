/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.servicio;

import com.aurahotel.demo.respuesta.RespuestaPago;
import com.stripe.exception.StripeException;
import com.stripe.model.Order;
import com.stripe.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioPago {
    public RespuestaPago crearMetodoPago(int amount) throws StripeException;
}
