package com.cultiva.webapp.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.cultiva.webapp.security.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/settings/account")
public class AccountController {
  private final AccountService accountService;

  @PostMapping(params = "change-password")
  public void changePassword(@Valid @RequestBody ChangePasswordDto input,
      @AuthenticationPrincipal UserPrincipal principal) {
    accountService.chagePassword(input, principal);
  }

  @PostMapping(params = "delete-account")
  public void deleteAccount(@AuthenticationPrincipal UserPrincipal principal, HttpServletRequest request) {
    accountService.deleteAccount(principal);
    request.getSession().invalidate();
  }

}
