package com.eazybytes.springsecsection1.controller;

import com.eazybytes.springsecsection1.constants.ApplicationConstants;
import com.eazybytes.springsecsection1.dto.LoginRequestDto;
import com.eazybytes.springsecsection1.model.Customer;
import com.eazybytes.springsecsection1.records.LoginResponse;
import com.eazybytes.springsecsection1.records.LogoutRequest;
import com.eazybytes.springsecsection1.records.LogoutResponse;
import com.eazybytes.springsecsection1.repo.CustomerRepository;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer) {
        try {
            String hashPwd = passwordEncoder.encode(customer.getPwd());
            customer.setPwd(hashPwd);

            boolean isEmailAlreadyRegistered = customerRepository.findByEmail(customer.getEmail()).isPresent();
            if (isEmailAlreadyRegistered) {
                return new ResponseEntity<>("Email already registered", HttpStatus.CONFLICT);
            }
            Customer savedCustomer = customerRepository.save(customer);

            if(savedCustomer.getId()>0) {
                return ResponseEntity.status(HttpStatus.CREATED).
                        body("Given user details are successfully registered");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                        body("User registration failed");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body("An exception occurred: " + ex.getMessage());
        }

    }

    @RequestMapping("/user")
    public Customer getUserDetailsAfterLogin(Authentication authentication) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(authentication.getName());
        return optionalCustomer.orElse(null);
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

//        BlacklistedTokens blacklistedTokens = BlacklistedTokens.builder()
//          .token(token)
//          .username(username)
//          .createdAt(Date.from(Instant.now()))
//          .updatedAt(Date.from(Instant.now()))
//          .build();
//
//        blacklistedTokensRepository.save(blacklistedTokens);


        return ResponseEntity.ok().body(new LogoutResponse(HttpStatus.OK.getReasonPhrase(), "Logged out successfully"));
    }
}
