package com.cultiva.webapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cultiva.webapp.security.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/settings")
public class SettingsController {

  @GetMapping
  public String settings() {
    return "redirect:/settings/profile";
  }

  @GetMapping("/profile")
  public String profile(@AuthenticationPrincipal UserPrincipal principal, Model model) {
    model.addAttribute("principal", principal);
    return "settings/profile";
  }

  @GetMapping("/account")
  public String account(@AuthenticationPrincipal UserPrincipal principal, Model model) {
    model.addAttribute("principal", principal);
    return "settings/account";
  }

  @ModelAttribute("currentUrl")
  public String getCurrentUrl(HttpServletRequest request) {
    return request.getRequestURI();
  }

}
