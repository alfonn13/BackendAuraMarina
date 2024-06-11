/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.controlador;

import com.aurahotel.demo.excepciones.ExceptionRolYaExiste;
import com.aurahotel.demo.modelo.Rol;
import com.aurahotel.demo.modelo.Usuario;
import com.aurahotel.demo.servicio.InterfazServicioRol;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de roles.
 * Proporciona endpoints para obtener, crear, eliminar y asignar roles.
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ControladorRol {
    private final InterfazServicioRol servicioRol;

    /**
     * Endpoint para obtener todos los roles.
     *
     * @return Una respuesta HTTP con una lista de todos los roles.
     */
    @GetMapping("/todos")
    public ResponseEntity<List<Rol>> obtenerTodosLosRoles() {
        return new ResponseEntity<>(servicioRol.obtenerRoles(), HttpStatus.FOUND);
    }

    /**
     * Endpoint para crear un nuevo rol.
     *
     * @param elRol El rol a crear.
     * @return Una respuesta HTTP indicando el resultado de la operación.
     */
    @PostMapping("/crear-nuevo-rol")
    public ResponseEntity<String> crearRol(@RequestBody Rol elRol) {
        try {
            servicioRol.crearRol(elRol);
            return ResponseEntity.ok("¡Nuevo rol creado exitosamente!");
        } catch (ExceptionRolYaExiste re) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(re.getMessage());
        }
    }

    /**
     * Endpoint para eliminar un rol.
     *
     * @param idRol El ID del rol a eliminar.
     */
    @DeleteMapping("/eliminar/{idRol}")
    public void eliminarRol(@PathVariable("idRol") Long idRol) {
        servicioRol.eliminarRol(idRol);
    }

    /**
     * Endpoint para eliminar todos los usuarios de un rol.
     *
     * @param idRol El ID del rol.
     * @return El rol después de eliminar todos los usuarios.
     */
    @PostMapping("/eliminar-todos-los-usuarios-de-rol/{idRol}")
    public Rol eliminarTodosLosUsuariosDeRol(@PathVariable("idRol") Long idRol) {
        return servicioRol.eliminarTodosLosUsuariosDeRol(idRol);
    }

    /**
     * Endpoint para eliminar un usuario de un rol.
     *
     * @param idUsuario El ID del usuario.
     * @param idRol El ID del rol.
     * @return El usuario después de ser eliminado del rol.
     */
    @PostMapping("/eliminar-usuario-de-rol")
    public Usuario eliminarUsuarioDeRol(
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam("idRol") Long idRol) {
        return servicioRol.eliminarUsuarioDeRol(idUsuario, idRol);
    }

    /**
     * Endpoint para asignar un usuario a un rol.
     *
     * @param idUsuario El ID del usuario.
     * @param idRol El ID del rol.
     * @return El usuario después de ser asignado al rol.
     */
    @PostMapping("/asignar-usuario-a-rol")
    public Usuario asignarUsuarioARol(
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam("idRol") Long idRol) {
        return servicioRol.asignarRolAUsuario(idUsuario, idRol);
    }
}

