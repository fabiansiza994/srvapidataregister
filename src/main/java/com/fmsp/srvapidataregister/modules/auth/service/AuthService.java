package com.fmsp.srvapidataregister.modules.auth.service;

import com.fmsp.srvapidataregister.core.exceptions.ForbiddenException;
import com.fmsp.srvapidataregister.core.exceptions.UnauthorizedException;
import com.fmsp.srvapidataregister.modules.auth.dto.AuthResponse;
import com.fmsp.srvapidataregister.modules.auth.dto.LoginRequest;
import com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO;
import com.fmsp.srvapidataregister.modules.users.service.IUsuarioService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final IUsuarioService usuarioService;
    private static final int MAX_INTENTOS_FALLIDOS = 5;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, IUsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    public AuthResponse login(LoginRequest request) {
        UsuarioDTO usuarioDTO = usuarioService.getUsuarioByUsername(request.getUsername().trim())
                .orElseThrow(() -> new UnauthorizedException("Usuario o contraseña inválidos"));

        if (usuarioDTO.isBloqueado()) {
            throw new ForbiddenException("Tu cuenta está bloqueada por múltiples intentos fallidos. Contacta al administrador.");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));


            usuarioService.updateBlockValue(0, false, usuarioDTO.getId());

            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtService.generateToken(user);

            return new AuthResponse(token, user.getUsername(), user.getAuthorities().toString());
        } catch (BadCredentialsException e) {
            usuarioDTO.setIntentosFallidos(usuarioDTO.getIntentosFallidos() + 1);

            if (usuarioDTO.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
                usuarioService.updateBlockValue(usuarioDTO.getIntentosFallidos(), true, usuarioDTO.getId());
                throw new ForbiddenException("Has excedido el número máximo de intentos. Tu cuenta ha sido bloqueada.");
            }

            usuarioService.updateBlockValue(usuarioDTO.getIntentosFallidos(), false, usuarioDTO.getId());

            throw new UnauthorizedException(String.format(
                    "Usuario o contraseña inválidos. Intento %d de %d",
                    usuarioDTO.getIntentosFallidos(),
                    MAX_INTENTOS_FALLIDOS
            ));
        } catch (LockedException e) {
            throw new ForbiddenException("Tu cuenta está bloqueada. Contacta al administrador.");
        }
    }
}