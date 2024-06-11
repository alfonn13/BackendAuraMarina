/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.respuesta;

import lombok.Data;

@Data
public class RespuestaPago {
    private String pago_url;
    private String error;

    public RespuestaPago() {
    }

    public RespuestaPago(String pago_url) {
        this.pago_url = pago_url;
    }

    public RespuestaPago(String pago_url, String error) {
        this.pago_url = pago_url;
        this.error = error;
    }

    public String getSessionId() {
        return pago_url;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
