/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.pedido;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String correo;
    private String contrasena;

}
