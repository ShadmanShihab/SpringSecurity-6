package com.eazybytes.springsecsection1.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpDto {
  private String username;
  private String email;
  private String password;
}
