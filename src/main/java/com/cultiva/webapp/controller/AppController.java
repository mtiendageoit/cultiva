package com.cultiva.webapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cultiva.webapp.config.AppConfig;
import com.cultiva.webapp.crop.CropService;
import com.cultiva.webapp.exception.*;
import com.cultiva.webapp.indices.*;
import com.cultiva.webapp.security.UserPrincipal;
import com.cultiva.webapp.user.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class AppController {
  private final AppConfig config;
  private final UserService userService;
  private final CropService cropService;
  private final IndiceService indiceService;

  @GetMapping("/")
  public String home(@AuthenticationPrincipal UserPrincipal principal, Model model) {
    model.addAttribute("principal", principal);
    model.addAttribute("crops", cropService.crops());
    model.addAttribute("indices", indiceService.indices());
    model.addAttribute("imagesUrl", config.getGoogleCloudStorageImagesUrl());
    return "home";
  }

  @GetMapping("/login")
  public String login(@AuthenticationPrincipal UserPrincipal principal) {
    if (principal != null) {
      return "redirect:/";
    }
    return "login";
  }

  @GetMapping("/signup")
  public String signup(Model model) {
    RegisterUserDto user = new RegisterUserDto();
    model.addAttribute("user", user);
    return "signup";
  }

  @PostMapping("/signup")
  public String postSignup(@ModelAttribute("user") @Valid RegisterUserDto input, Model model,
      HttpServletRequest request) {

    try {
      String appUrl = getAppUrl(request);
      userService.registerByEmail(input, appUrl);
    } catch (AlreadyExistsException e) {
      model.addAttribute("error", "An account already exists for this email.");
      model.addAttribute("user", input);
      return "signup";
    }

    model.addAttribute("email", input.getEmail());
    return "signup-success";
  }

  @GetMapping("/verify")
  public String verifyRegistrationCode(@RequestParam String token) {
    try {
      userService.verifyRegistration(token);
    } catch (TokenNotFoundException | InvalidVerificacionTokenException e) {
      return "verify-error";
    }

    return "verify-success";
  }

  @GetMapping("/password-reset")
  public String passwordReset() {
    return "password-reset";
  }

  @PostMapping("/password-reset")
  public String postPasswordReset(String email, HttpServletRequest request) {
    String appUrl = getAppUrl(request);
    userService.resetPassword(email, appUrl);
    return "redirect:/password-reset-success";
  }

  @GetMapping("/password-reset-success")
  public String passwordResetSuccess() {
    return "password-reset-success";
  }

  @GetMapping("/change-password")
  public String changePassword(@RequestParam String token, Model model) {
    boolean isValid = userService.isResetPasswordTokenValid(token);

    if (!isValid) {
      return "change-password-error";
    }

    model.addAttribute("token", token);

    return "change-password";
  }

  @PostMapping("/change-password")
  public String postChangePassword(String token, String password, Model model) {

    try {
      userService.changePassword(token, password);
    } catch (TokenNotFoundException e) {
      model.addAttribute(token, token);
      return "change-password-error";
    }

    return "change-password-success";
  }

  private String getAppUrl(HttpServletRequest request) {
    return ServletUriComponentsBuilder.fromRequestUri(request)
        .replacePath(null)
        .build()
        .toUriString();
  }
}
