/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.seguridad.usuario;


import com.aurahotel.demo.modelo.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetallesUsuarioHotel implements UserDetails {
    private Long id;
    private  String correo;
    private String contrasena;
    private Collection<GrantedAuthority> authorities;

    public static DetallesUsuarioHotel buildUserDetails(Usuario user){
        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getNombre()))
                .collect(Collectors.toList());
        return new DetallesUsuarioHotel(
                user.getId(),
                user.getCorreo(),
                user.getContrasena(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
