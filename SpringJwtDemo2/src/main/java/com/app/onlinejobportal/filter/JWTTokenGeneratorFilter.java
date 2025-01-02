package com.app.onlinejobportal.filter;

import com.app.onlinejobportal.ApplicationConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.util.stream.Collectors;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      Environment env = getEnvironment();
      if (env != null) {
        String secret = env.getProperty(ApplicationConstants.JWT_SECRET, ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String jwtToken = Jwts.builder().issuer(ApplicationConstants.JWT_SECRET).subject("JWT TOKEN")
          .claim("username", authentication.getName())
          .claim("authorities", authentication.getAuthorities().stream()
            .map(authority -> "ROLE_" + authority.getAuthority())
            .collect(Collectors.joining(","))
          )
          .issuedAt(Date.from(Instant.now()))
          .expiration(Date.from(Instant.now().plusMillis(30000000)))
          .signWith(secretKey)
          .compact();

        response.addHeader("Authorization", jwtToken);
      }
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getServletPath().equals("/login");
  }
}
