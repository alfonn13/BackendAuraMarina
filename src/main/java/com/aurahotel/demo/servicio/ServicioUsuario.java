/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.servicio;

import com.aurahotel.demo.excepciones.ExcepcionNoEncuentraUsuario;
import com.aurahotel.demo.excepciones.ExceptionUsuarioYaExiste;
import com.aurahotel.demo.modelo.Rol;
import com.aurahotel.demo.modelo.Usuario;
import com.aurahotel.demo.repositorio.RepositorioRol;
import com.aurahotel.demo.repositorio.RepositorioUsuario;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioUsuario implements InterfazServicioUsuario{
    private final RepositorioUsuario usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final RepositorioRol rolRepositorio;

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepositorio.existsByCorreo(usuario.getCorreo())) {
            throw new ExceptionUsuarioYaExiste(usuario.getCorreo() + " ya existe");
        }
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        System.out.println(usuario.getContrasena());

        Rol rolUsuario = (Rol) rolRepositorio.findByNombre("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setRoles(Collections.singletonList(rolUsuario));
        return usuarioRepositorio.save(usuario);
    }

    @Override
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepositorio.findAll();
    }

    @Transactional
    @Override
    public void eliminarUsuario(String correo) {
        Usuario elUsuario = obtenerUsuario(correo);
        if (elUsuario != null){
            usuarioRepositorio.deleteByCorreo(correo);
        }
    }

    @Override
    public Usuario obtenerUsuario(String correo) {
        return usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new ExcepcionNoEncuentraUsuario("Usuario no encontrado"));
    }
}

