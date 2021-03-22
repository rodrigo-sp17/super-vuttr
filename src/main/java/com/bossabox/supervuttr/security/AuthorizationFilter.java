package com.bossabox.supervuttr.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final String jwtSecret;

    public AuthorizationFilter(AuthenticationManager authenticationManager,
                               String jwtSecret) {
        super(authenticationManager);
        this.jwtSecret = jwtSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        var header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        var authenticationToken = parseToken(header);
        SecurityContextHolder.getContext()
                .setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken parseToken(String header) {
        var stringToken = header.replace("Bearer ", "");

        var decodedJWT = JWT.require(Algorithm.HMAC512(jwtSecret)).build()
                .verify(stringToken);

        var username = decodedJWT.getSubject();
        var userId = decodedJWT.getClaim("userid").asString();

        if (userId == null || username == null) {
            return null;
        }

        UserPrincipal userPrincipal = new UserPrincipal(username,
                "", Collections.emptyList(), userId);

        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                Collections.emptyList()
        );
    }
}
