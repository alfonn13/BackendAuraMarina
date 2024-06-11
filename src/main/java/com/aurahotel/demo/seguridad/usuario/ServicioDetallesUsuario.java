/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.seguridad.usuario;

import com.aurahotel.demo.modelo.Usuario;
import com.aurahotel.demo.repositorio.RepositorioUsuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioDetallesUsuario implements UserDetailsService {

    private final RepositorioUsuario repositorioUsuario;
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = repositorioUsuario.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return DetallesUsuarioHotel.buildUserDetails(usuario);
    }
}

