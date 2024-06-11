package com.aurahotel.demo.controlador;

import com.aurahotel.demo.excepciones.ExceptionRolYaExiste;
import com.aurahotel.demo.modelo.Rol;
import com.aurahotel.demo.modelo.Usuario;
import com.aurahotel.demo.servicio.InterfazServicioRol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorRol.class)
public class ControladorRolTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterfazServicioRol servicioRol;

    private Rol rol;
    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ROLE_USER");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("test@test.com");
    }

    @Test
    public void testObtenerTodosLosRoles() throws Exception {
        List<Rol> roles = List.of(rol);

        Mockito.when(servicioRol.obtenerRoles()).thenReturn(roles);

        mockMvc.perform(get("/roles/todos"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$[0].nombre").value("ROLE_USER"));
    }

    @Test
    public void testCrearRol() throws Exception {
        Mockito.doNothing().when(servicioRol).crearRol(any(Rol.class));

        mockMvc.perform(post("/roles/crear-nuevo-rol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"ROLE_USER\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("¡Nuevo rol creado exitosamente!"));
    }

    @Test
    public void testCrearRolYaExiste() throws Exception {
        Mockito.doThrow(new ExceptionRolYaExiste("El rol ya existe")).when(servicioRol).crearRol(any(Rol.class));

        mockMvc.perform(post("/roles/crear-nuevo-rol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"ROLE_USER\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("El rol ya existe"));
    }

    @Test
    public void testEliminarRol() throws Exception {
        mockMvc.perform(delete("/roles/eliminar/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(servicioRol, Mockito.times(1)).eliminarRol(1L);
    }

    @Test
    public void testEliminarTodosLosUsuariosDeRol() throws Exception {
        Mockito.when(servicioRol.eliminarTodosLosUsuariosDeRol(anyLong())).thenReturn(rol);

        mockMvc.perform(post("/roles/eliminar-todos-los-usuarios-de-rol/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("ROLE_USER"));
    }

    @Test
    public void testEliminarUsuarioDeRol() throws Exception {
        Mockito.when(servicioRol.eliminarUsuarioDeRol(anyLong(), anyLong())).thenReturn(usuario);

        mockMvc.perform(post("/roles/eliminar-usuario-de-rol")
                        .param("idUsuario", "1")
                        .param("idRol", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("test@test.com"));
    }

    @Test
    public void testAsignarUsuarioARol() throws Exception {
        Mockito.when(servicioRol.asignarRolAUsuario(anyLong(), anyLong())).thenReturn(usuario);

        mockMvc.perform(post("/roles/asignar-usuario-a-rol")
                        .param("idUsuario", "1")
                        .param("idRol", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("test@test.com"));
    }
}
