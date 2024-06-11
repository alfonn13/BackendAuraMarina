/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.repositorio;

import com.aurahotel.demo.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositorioRol extends JpaRepository<Rol, Long> {

    Optional<Object> findByNombre(String roleUsuario);

    boolean existsByNombre(String nombreRol);
}
