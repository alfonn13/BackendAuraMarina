/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.repositorio;

import com.aurahotel.demo.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {
    boolean existsByCorreo(String correo);

    void deleteByCorreo(String correo);

    Optional<Usuario> findByCorreo(String correo);



}

