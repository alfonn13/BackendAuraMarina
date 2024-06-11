/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.servicio;

import com.aurahotel.demo.excepciones.ExcepcionNoEncuentraUsuario;
import com.aurahotel.demo.excepciones.ExceptionRolYaExiste;
import com.aurahotel.demo.excepciones.ExceptionUsuarioYaExiste;
import com.aurahotel.demo.modelo.Rol;
import com.aurahotel.demo.modelo.Usuario;
import com.aurahotel.demo.repositorio.RepositorioRol;
import com.aurahotel.demo.repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicioRol implements InterfazServicioRol {

    private final RepositorioRol repositorioRol;
    private final RepositorioUsuario repositorioUsuario;

    @Override
    public List<Rol> obtenerRoles() {
        return repositorioRol.findAll();
    }

    @Override
    public Rol crearRol(Rol elRol) throws ExceptionRolYaExiste {
        String nombreRol = "ROL_" + elRol.getNombre().toUpperCase();
        Rol rol = new Rol(nombreRol);
        if (repositorioRol.existsByNombre(nombreRol)) {
            throw new ExceptionRolYaExiste(elRol.getNombre() + " rol ya existe");
        }
        return repositorioRol.save(rol);
    }

    @Override
    public void eliminarRol(Long idRol) {
        this.eliminarTodosLosUsuariosDeRol(idRol);
        repositorioRol.deleteById(idRol);
    }

    @Override
    public Rol buscarPorNombre(String nombre) {
        return (Rol) repositorioRol.findByNombre(nombre).get();
    }

    @Override
    public Usuario eliminarUsuarioDeRol(Long idUsuario, Long idRol) {
        Optional<Usuario> usuario = repositorioUsuario.findById(idUsuario);
        Optional<Rol> rol = repositorioRol.findById(idRol);
        if (rol.isPresent() && rol.get().getUsuarios().contains(usuario.get())) {
            rol.get().borrarUsuarioDeRol(usuario.get());
            repositorioRol.save(rol.get());
            return usuario.get();
        }
            throw new ExcepcionNoEncuentraUsuario("Usuario no encontrado");
    }

    @Override
    public Usuario asignarRolAUsuario(Long idUsuario, Long idRol) {
        Optional<Usuario> usuario = repositorioUsuario.findById(idUsuario);
        Optional<Rol> rol = repositorioRol.findById(idRol);
        if (usuario.isPresent() && usuario.get().getRoles().contains(rol.get())) {
            throw new ExceptionUsuarioYaExiste(
                    usuario.get().getNombre() + " ya está asignado al rol " + rol.get().getNombre());
        }
        if (rol.isPresent()) {
            rol.get().asignarRolAUsuario(usuario.get());
            repositorioRol.save(rol.get());
        }
        return usuario.get();
    }

    @Override
    public Rol eliminarTodosLosUsuariosDeRol(Long idRol) {
        Optional<Rol> rol = repositorioRol.findById(idRol);
        rol.ifPresent(Rol::borrarTodosLosUsuariosDelRol);
        return repositorioRol.save(rol.get());
    }
}
