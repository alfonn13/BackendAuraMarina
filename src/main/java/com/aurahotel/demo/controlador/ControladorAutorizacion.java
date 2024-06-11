/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.controlador;

import com.aurahotel.demo.excepciones.ExceptionUsuarioYaExiste;
import com.aurahotel.demo.modelo.Usuario;
import com.aurahotel.demo.pedido.LoginRequest;
import com.aurahotel.demo.respuesta.JwtRespuesta;
import com.aurahotel.demo.seguridad.jwt.JwtUtils;
import com.aurahotel.demo.seguridad.usuario.DetallesUsuarioHotel;
import com.aurahotel.demo.servicio.InterfazServicioUsuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la autorización de usuarios.
 * Proporciona endpoints para el registro de usuarios y el inicio de sesión.
 */
@RestController
@RequestMapping("/autorizacion")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ControladorAutorizacion {
    private final InterfazServicioUsuario servicioUsuario;
    private final AuthenticationManager administradorAutenticacion;
    private final JwtUtils utilidadesJwt;

    /**
     * Endpoint para registrar un nuevo usuario.
     *
     * @param usuario El usuario a registrar.
     * @return Una respuesta HTTP indicando el resultado del registro.
     */
    @PostMapping("/registrar-usuario")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            servicioUsuario.registrarUsuario(usuario);
            return ResponseEntity.ok("¡Registro exitoso!");
        } catch (ExceptionUsuarioYaExiste e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Endpoint para autenticar a un usuario existente.
     *
     * @param solicitud La solicitud de inicio de sesión que contiene las credenciales del usuario.
     * @return Una respuesta HTTP con el token JWT si la autenticación es exitosa.
     */
    @PostMapping("/inicio-sesion")
    public ResponseEntity<?> autenticarUsuario(@Valid @RequestBody LoginRequest solicitud) {
        Authentication autenticacion =
                administradorAutenticacion.authenticate(
                        new UsernamePasswordAuthenticationToken(solicitud.getCorreo(), solicitud.getContrasena()));
        SecurityContextHolder.getContext().setAuthentication(autenticacion);
        String jwt = utilidadesJwt.generateJwtTokenForUser(autenticacion);
        DetallesUsuarioHotel detallesUsuario = (DetallesUsuarioHotel) autenticacion.getPrincipal();
        List<String> roles = detallesUsuario.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtRespuesta jwtRespuesta = new JwtRespuesta(
                detallesUsuario.getId(),
                detallesUsuario.getCorreo(),
                jwt,
                roles);

        if (jwtRespuesta.getJwt() == null || jwtRespuesta.getJwt().isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el token JWT");
        }

        return ResponseEntity.ok(jwtRespuesta);
    }
}
