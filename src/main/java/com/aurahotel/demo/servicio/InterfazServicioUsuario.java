/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.servicio;

import com.aurahotel.demo.modelo.Usuario;

import java.util.List;

public interface InterfazServicioUsuario {
    Usuario registrarUsuario(Usuario usuario);
    List<Usuario> obtenerUsuarios();
    void eliminarUsuario(String correo);
    Usuario obtenerUsuario(String correo);
}
