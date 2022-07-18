package pl.kurs.config.security.utils;

import com.auth0.jwt.JWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import pl.kurs.config.security.model.AppUser;
import pl.kurs.config.security.configuration.JwtConfigurationProperties;
import pl.kurs.config.security.model.Role;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
public class JwtUtil {

    private final JwtConfigurationProperties properties;

    public String generateAccessToken(HttpServletRequest request, User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + properties.getExpirationTime()))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(properties.algorithm());
    }

    public String generateRefreshToken(HttpServletRequest request, User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + properties.getExpirationTime()))
                .withIssuer(request.getRequestURL().toString())
                .sign(properties.algorithm());
    }

    public String generateAccessTokenWithAppUser(HttpServletRequest request, AppUser user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + properties.getExpirationTime()))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getRoles()
                        .stream()
                        .map(Role::getName).collect(Collectors.toList()))
                .sign(properties.algorithm());
    }

}
