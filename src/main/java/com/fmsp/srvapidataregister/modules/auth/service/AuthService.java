package com.fmsp.srvapidataregister.modules.auth.service;

import com.fmsp.srvapidataregister.core.exceptions.ForbiddenException;
import com.fmsp.srvapidataregister.core.exceptions.UnauthorizedException;
import com.fmsp.srvapidataregister.modules.auth.dto.AuthResponse;
import com.fmsp.srvapidataregister.modules.auth.dto.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            UserDetails user = (UserDetails) auth.getPrincipal();

            if (!user.isEnabled()) {
                throw new ForbiddenException("Usuario bloqueado");
            }

            String token = jwtService.generateToken(user);

            return new AuthResponse(token, user.getUsername(), user.getAuthorities().toString());
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Usuario o contraseña inválidos");
        }
    }
}