package com.app.onlinejobportal.config;

import com.app.onlinejobportal.exceptions.CustomAccessDeniedHandler;
import com.app.onlinejobportal.exceptions.CustomBasicAuthenticationEntryPoint;
import com.app.onlinejobportal.filter.JWTTokenGeneratorFilter;
import com.app.onlinejobportal.filter.JWTTokenValidatorFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

  @Autowired
  private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

    http.sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession").maximumSessions(1).maxSessionsPreventsLogin(true))
      .csrf(AbstractHttpConfigurer::disable)
//      .requiresChannel(rcc -> rcc.anyRequest().requiresSecure())
      .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
      .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
      .authorizeHttpRequests(request -> request
          .requestMatchers(HttpMethod.POST, "/company").hasRole("ADMIN")
//          .requestMatchers(HttpMethod.POST, "/company").hasAuthority("ADMIN")
          .requestMatchers(HttpMethod.GET, "/company").hasRole("USER")
          .requestMatchers("/register", "/apiLogin").permitAll()
        );
//    http.formLogin(withDefaults());
    http.exceptionHandling(ehc -> ehc
      .authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())
      .accessDeniedHandler(new CustomAccessDeniedHandler()));


//    http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
//    http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }

  @Bean
  public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider(userDetailsService, passwordEncoder);
    ProviderManager providerManager = new ProviderManager(authenticationProvider);
    providerManager.setEraseCredentialsAfterAuthentication(false);
    return providerManager;
  }
}
