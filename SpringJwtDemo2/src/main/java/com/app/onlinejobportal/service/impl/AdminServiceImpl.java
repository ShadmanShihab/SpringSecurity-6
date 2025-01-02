package com.app.onlinejobportal.service.impl;

import com.app.onlinejobportal.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminServiceImpl implements UserService {
  @Override
  public void register(String user) {
    log.info("Registering admin {}", user);
  }

  @Override
  public void login(String username, String password) {

  }
}
