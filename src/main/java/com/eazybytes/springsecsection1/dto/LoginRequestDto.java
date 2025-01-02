package com.eazybytes.springsecsection1.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
  private String username;
  private String password;
}
