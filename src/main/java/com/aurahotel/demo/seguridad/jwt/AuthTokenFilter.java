/**
 * @author Alfonso Reviejo Valle
 */
package com.aurahotel.demo.seguridad.jwt;

import com.aurahotel.demo.seguridad.usuario.ServicioDetallesUsuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ServicioDetallesUsuario servicioDetallesUsuario;
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try{

            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateToken(jwt)){
                String correo = jwtUtils.getUserNameFromToken(jwt);
                UserDetails userDetails = servicioDetallesUsuario.loadUserByUsername(correo);
                var authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }


        }catch (Exception e){
            logger.error("Cannot set user authentication : {} ", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7);
        }
        return null;
    }
}
