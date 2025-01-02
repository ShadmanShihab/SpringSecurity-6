package com.app.onlinejobportal.service.impl;

import com.app.onlinejobportal.model.User;
import com.app.onlinejobportal.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
  @Override
  public void register(String user) {
    log.info("Registering user: {}", user);
  }

  @Override
  public void login(String username, String password) {
    log.info("Login user: {}", username);
  }
}
