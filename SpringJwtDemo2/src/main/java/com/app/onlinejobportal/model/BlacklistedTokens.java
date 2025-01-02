package com.app.onlinejobportal.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Builder
@Table(name = "blacklisted_tokens")
public class BlacklistedTokens {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;

  private String username;

  private Date createdAt;

  private Date updatedAt;
}
