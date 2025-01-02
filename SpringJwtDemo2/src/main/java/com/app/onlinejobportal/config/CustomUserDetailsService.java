package com.app.onlinejobportal.config;

import com.app.onlinejobportal.model.User;
import com.app.onlinejobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmailOrUsername(username, username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));
    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
  }
}
