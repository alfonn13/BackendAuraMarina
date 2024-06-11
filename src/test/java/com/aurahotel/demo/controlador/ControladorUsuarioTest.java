package com.aurahotel.demo.controlador;

import com.aurahotel.demo.modelo.Usuario;
import com.aurahotel.demo.servicio.InterfazServicioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorUsuario.class)
public class ControladorUsuarioTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterfazServicioUsuario servicioUsuario;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("test@test.com");
        usuario.setContrasena("password");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testObtenerUsuarios() throws Exception {
        List<Usuario> usuarios = List.of(usuario);

        Mockito.when(servicioUsuario.obtenerUsuarios()).thenReturn(usuarios);

        mockMvc.perform(get("/usuarios/todos"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$[0].correo").value("test@test.com"));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void testObtenerUsuarioPorCorreo() throws Exception {
        Mockito.when(servicioUsuario.obtenerUsuario(anyString())).thenReturn(usuario);

        mockMvc.perform(get("/usuarios/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("test@test.com"));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void testObtenerUsuarioPorCorreoNoEncontrado() throws Exception {
        Mockito.when(servicioUsuario.obtenerUsuario(anyString())).thenThrow(new UsernameNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/usuarios/test@test.com"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no encontrado"));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void testObtenerUsuarioPorCorreoErrorInterno() throws Exception {
        Mockito.when(servicioUsuario.obtenerUsuario(anyString())).thenThrow(new RuntimeException("Error interno"));

        mockMvc.perform(get("/usuarios/test@test.com"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error al obtener el usuario"));
    }
}
