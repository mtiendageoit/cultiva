package com.cultiva.webapp.user;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

  @NotBlank
  private String fullname;
  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String password;
}
