package com.cultiva.webapp.user;

import java.util.Date;

import com.cultiva.webapp.security.AuthProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fullname;

  @Email
  @Column(nullable = false)
  private String email;

  private String avatar;

  private String phone;

  @JsonIgnore
  private String password;

  @JsonIgnore
  private boolean enabled;

  @JsonIgnore
  private String confirmationToken;

  @JsonIgnore
  @Temporal(TemporalType.TIMESTAMP)
  private Date confirmationTokenCreatedAt;

  @JsonIgnore
  private String resetPasswordToken;

  @JsonIgnore
  @Temporal(TemporalType.TIMESTAMP)
  private Date resetPasswordTokenCreatedAt;

  @JsonIgnore
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AuthProvider authProvider;

  @JsonIgnore
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;
}