package com.app.onlinejobportal.service;

import com.app.onlinejobportal.model.User;

public interface UserService {
  public void register(String user);

  public void login(String username, String password);
}
