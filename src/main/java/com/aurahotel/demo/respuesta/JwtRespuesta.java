/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.respuesta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtRespuesta {

    private Long id;
    private String correo;
    private String token;
    private String tipo = "Bearer";
    private List<String> roles;

    public JwtRespuesta(Long id, String correo, String jwt, List<String> roles) {
        this.id = id;
        this.correo = correo;
        this.token = jwt;
        this.roles = roles;
    }

    public String getJwt() {
        return this.token;
    }
}
