package com.fmsp.srvapidataregister.modules.auth.service;

import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import com.fmsp.srvapidataregister.modules.users.repository.UsuarioRepository;
import com.fmsp.srvapidataregister.core.util.LoadDataProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final UsuarioRepository usuarioRepository;
    private final LoadDataProperties loadDataProperties;

    public JwtService(UsuarioRepository usuarioRepository, LoadDataProperties loadDataProperties) {
        this.usuarioRepository = usuarioRepository;
        this.loadDataProperties = loadDataProperties;
    }

    public String generateToken(UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByUsuario(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", usuario.getRol().getNombre());
        claims.put("empresa", usuario.getGrupo().getEmpresa().getNombre());

        claims.put("userId", usuario.getId());
        claims.put("empresaId", usuario.getGrupo().getEmpresa().getId());
        claims.put("grupoId", usuario.getGrupo().getId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getUsuario())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(loadDataProperties.getExpirationMinutes(), ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(loadDataProperties.getSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(loadDataProperties.getSecret().getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()));
    }
}