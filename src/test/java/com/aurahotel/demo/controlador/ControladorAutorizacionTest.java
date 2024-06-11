package com.aurahotel.demo.controlador;

import com.aurahotel.demo.excepciones.ExceptionUsuarioYaExiste;
import com.aurahotel.demo.modelo.Usuario;
import com.aurahotel.demo.pedido.LoginRequest;
import com.aurahotel.demo.respuesta.JwtRespuesta;
import com.aurahotel.demo.seguridad.jwt.JwtUtils;
import com.aurahotel.demo.seguridad.usuario.DetallesUsuarioHotel;
import com.aurahotel.demo.servicio.InterfazServicioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ControladorAutorizacion.class)
public class ControladorAutorizacionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterfazServicioUsuario servicioUsuario;

    @MockBean
    private AuthenticationManager administradorAutenticacion;

    @MockBean
    private JwtUtils utilidadesJwt;

    private Usuario usuario;
    private LoginRequest loginRequest;
    private JwtRespuesta jwtRespuesta;
    private DetallesUsuarioHotel detallesUsuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("test@test.com");
        usuario.setContrasena("password");

        loginRequest = new LoginRequest();
        loginRequest.setCorreo("test@test.com");
        loginRequest.setContrasena("password");

        detallesUsuario = DetallesUsuarioHotel.buildUserDetails(usuario);

        jwtRespuesta = new JwtRespuesta(
                usuario.getId(),
                usuario.getCorreo(),
                "mockedJwtToken",
                List.of("ROLE_USER")
        );
    }

    @Test
    public void testRegistrarUsuario() throws Exception {
        // Mockea el método para no hacer nada y retornar vacío (null)
        Mockito.doNothing().when(servicioUsuario).registrarUsuario(any(Usuario.class));

        mockMvc.perform(post("/autorizacion/registrar-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"test@test.com\", \"contrasena\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("¡Registro exitoso!"));
    }

    @Test
    public void testRegistrarUsuarioYaExiste() throws Exception {
        Mockito.doThrow(new ExceptionUsuarioYaExiste("El usuario ya existe"))
                .when(servicioUsuario).registrarUsuario(any(Usuario.class));

        mockMvc.perform(post("/autorizacion/registrar-usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"test@test.com\", \"contrasena\":\"password\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("El usuario ya existe"));
    }

    @Test
    public void testAutenticarUsuario() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(administradorAutenticacion.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(detallesUsuario);
        Mockito.when(utilidadesJwt.generateJwtTokenForUser(any(Authentication.class))).thenReturn("mockedJwtToken");

        mockMvc.perform(post("/autorizacion/inicio-sesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"test@test.com\", \"contrasena\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"correo\":\"test@test.com\",\"jwt\":\"mockedJwtToken\",\"roles\":[\"ROLE_USER\"]}"));
    }

    @Test
    public void testAutenticarUsuarioFalla() throws Exception {
        Mockito.when(administradorAutenticacion.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        mockMvc.perform(post("/autorizacion/inicio-sesion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"test@test.com\", \"contrasena\":\"password\"}"))
                .andExpect(status().isUnauthorized());
    }
}
