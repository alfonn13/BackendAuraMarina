/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.controlador;


import com.aurahotel.demo.excepciones.ExcepcionNoEncuentraUsuario;
import com.aurahotel.demo.modelo.Usuario;
import com.aurahotel.demo.servicio.InterfazServicioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de usuarios.
 * Proporciona endpoints para obtener todos los usuarios y obtener un usuario por correo.
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ControladorUsuario {
    private final InterfazServicioUsuario servicioUsuario;

    /**
     * Endpoint para obtener todos los usuarios.
     *
     * @return Una respuesta HTTP con una lista de todos los usuarios.
     */
    @GetMapping("/todos")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Usuario>> obtenerUsuarios(){
        return new ResponseEntity<>(servicioUsuario.obtenerUsuarios(), HttpStatus.FOUND);
    }

    /**
     * Endpoint para obtener un usuario por su correo.
     *
     * @param correo El correo del usuario a obtener.
     * @return Una respuesta HTTP con los detalles del usuario solicitado.
     */
    @GetMapping("/{correo}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> obtenerUsuarioPorCorreo(@PathVariable("correo") String correo){
        try{
            Usuario elUsuario = servicioUsuario.obtenerUsuario(correo);
            return ResponseEntity.ok(elUsuario);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el usuario");
        }
    }
}

