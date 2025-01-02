package com.app.onlinejobportal.controller;

import com.app.onlinejobportal.ApplicationConstants;
import com.app.onlinejobportal.dto.LoginRequestDto;
import com.app.onlinejobportal.dto.SignUpDto;
import com.app.onlinejobportal.model.BlacklistedTokens;
import com.app.onlinejobportal.model.User;
import com.app.onlinejobportal.records.LoginResponse;
import com.app.onlinejobportal.records.LogoutRequest;
import com.app.onlinejobportal.records.LogoutResponse;
import com.app.onlinejobportal.repository.BlacklistedTokensRepository;
import com.app.onlinejobportal.repository.RoleRepository;
import com.app.onlinejobportal.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AuthController {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AuthenticationManager authenticationManager;
  private final Environment env;
  private final PasswordEncoder passwordEncoder;
  private final BlacklistedTokensRepository blacklistedTokensRepository;

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody SignUpDto dto) {
    try {
      boolean isUserAlreadyExists = userRepository.findByEmailOrUsername(dto.getEmail(), dto.getUsername()).isPresent();
      if (isUserAlreadyExists) {
        return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
      }

      String hashPwd = passwordEncoder.encode(dto.getPassword());

      User user = new User();
      user.setEmail(dto.getEmail());
      user.setPassword(hashPwd);
      user.setUsername(dto.getUsername());
      user.setRole(roleRepository.getById((long) 1));
      user.setCreatedAt(new Date(System.currentTimeMillis()));
      user.setUpdatedAt(new Date(System.currentTimeMillis()));

      user = userRepository.save(user);

      if (user.getId() > 0) {
        return new ResponseEntity<>("User successfully registered", HttpStatus.CREATED);
      } else {
        return new ResponseEntity<>("User could not be registered", HttpStatus.CONFLICT);
      }

    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/apiLogin")
  public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequestDto dto) {
    String jwtToken = "";
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(dto.getUsername(), dto.getPassword());
    Authentication authResponse = authenticationManager.authenticate(authentication);
    if (authResponse != null && authResponse.isAuthenticated()) {
      String secret = env.getProperty(ApplicationConstants.JWT_SECRET, ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
      SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
      jwtToken = Jwts.builder().issuer(ApplicationConstants.JWT_SECRET).subject("JWT TOKEN")
        .claim("username", authResponse.getName())
        .claim("authorities", authResponse.getAuthorities().stream()
//          .map(GrantedAuthority::getAuthority)
            .map(authority -> "ROLE_" + authority.getAuthority())
            .collect(Collectors.joining(","))
        )
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plusMillis(30000000)))
        .signWith(secretKey)
        .compact();
    }
    return ResponseEntity.ok().header(ApplicationConstants.JWT_HEADER, jwtToken)
      .body(new LoginResponse(HttpStatus.OK.getReasonPhrase(), "Logged in successfully", jwtToken));
  }

  @PostMapping("/logout")
  public ResponseEntity<LogoutResponse> logoutUser(@RequestBody LogoutRequest logoutRequest) {
    String token = logoutRequest.token();
    String username = logoutRequest.userName();
    BlacklistedTokens blacklistedTokens = BlacklistedTokens.builder()
      .token(token)
      .username(username)
      .createdAt(Date.from(Instant.now()))
      .updatedAt(Date.from(Instant.now()))
      .build();

    blacklistedTokensRepository.save(blacklistedTokens);
    return ResponseEntity.ok().body(new LogoutResponse(HttpStatus.OK.getReasonPhrase(), "Logged out successfully"));
  }
}
