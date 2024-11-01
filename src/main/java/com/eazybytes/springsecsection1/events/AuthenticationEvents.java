package com.eazybytes.springsecsection1.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationEvents {
  @EventListener
  public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
    log.info("onAuthenticationSuccess {}", event.getAuthentication().getName());
  }

  @EventListener
  public void onFailure(AbstractAuthenticationFailureEvent event) {
    log.info("Login failed for the user : {}, due to {}",
      event.getAuthentication().getName(),
      event.getException().getMessage());
  }
}

