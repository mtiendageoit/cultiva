package com.cultiva.webapp.account;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordDto {
  @NotBlank
  private String password;
  
  @NotBlank
  private String newPassword;
}
