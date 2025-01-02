package com.app.onlinejobportal.filter;

import com.app.onlinejobportal.ApplicationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String jwtToken = request.getHeader("Authorization");
    if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
      try {
        jwtToken = jwtToken.substring(7);
        Environment env = getEnvironment();
        if (env != null) {
          String secret = env.getProperty(ApplicationConstants.JWT_SECRET, ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
          SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
          if (secretKey != null) {
            Claims claims = Jwts.parser().verifyWith(secretKey)
              .build().parseSignedClaims(jwtToken).getBody();
            if (claims != null) {
              String username = String.valueOf(claims.get("username"));
              String authorities = String.valueOf(claims.get("authorities"));
              Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
              SecurityContextHolder.getContext().setAuthentication(auth);
            }
          }
        }
      } catch (Exception e) {
        throw new BadCredentialsException("Invalid token");
      }
    }
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getServletPath().equals("/login");
  }
}
