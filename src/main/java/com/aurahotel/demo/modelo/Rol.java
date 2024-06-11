/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Collection<Usuario> usuarios = new HashSet<>();

    public Rol(String nombre) {
        this.nombre = nombre;
    }

    public void asignarRolAUsuario(Usuario usuario){
        usuario.getRoles().add(this);
        this.getUsuarios().add(usuario);
    }

    public void borrarUsuarioDeRol(Usuario usuario){
        usuario.getRoles().remove(this);
        this.getUsuarios().remove(usuario);
    }

    public void borrarTodosLosUsuariosDelRol(){
        if (this.getUsuarios() != null){
            List<Usuario> usuariosDelRol = this.getUsuarios().stream().collect(Collectors.toList());
            usuariosDelRol.forEach(this::borrarUsuarioDeRol);
        }
    }

    public String getNombre(){
        return nombre != null ? nombre : "";
    }



}

