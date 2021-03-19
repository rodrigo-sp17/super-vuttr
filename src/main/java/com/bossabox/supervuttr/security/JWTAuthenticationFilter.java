package com.bossabox.supervuttr.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bossabox.supervuttr.data.AppUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager manager;

    private final String jwtSecret;

    private final Long jwtExpiration;

    private final ObjectMapper mapper = new ObjectMapper();

    public JWTAuthenticationFilter(AuthenticationManager manager,
                                   String jwtSecret,
                                   Long jwtExpiration) {
        this.manager = manager;
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        try {
            var user = mapper.readValue(request.getReader(), AppUser.class);
            var token = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword()
            );
            return manager.authenticate(token);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Could not read user data from request");
        } catch (AuthenticationException a) {
            log.info("Failed authentication: " + a.getMessage());
            throw a;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {
        var userPrincipal = (UserPrincipal) authResult.getPrincipal();

        var jwtToken = JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withClaim("userid", userPrincipal.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration))
                .sign(Algorithm.HMAC512(jwtSecret.getBytes()));

        response.addHeader("Authorization", "Bearer " + jwtToken);
    }
}
