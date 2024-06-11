/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.servicio;

import com.aurahotel.demo.excepciones.ExceptionRolYaExiste;
import com.aurahotel.demo.modelo.Rol;
import com.aurahotel.demo.modelo.Usuario;

import java.util.List;

public interface InterfazServicioRol {
    List<Rol> obtenerRoles();

    Rol crearRol(Rol elRol) throws ExceptionRolYaExiste;

    void eliminarRol(Long id);

    Rol buscarPorNombre(String nombre);

    Usuario eliminarUsuarioDeRol(Long idUsuario, Long idRol);

    Usuario asignarRolAUsuario(Long idUsuario, Long idRol);

    Rol eliminarTodosLosUsuariosDeRol(Long idRol);

}
