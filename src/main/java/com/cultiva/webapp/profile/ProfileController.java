package com.cultiva.webapp.profile;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cultiva.webapp.security.UserPrincipal;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/settings/profile")
public class ProfileController {
  private final ProfileService profileService;

  @GetMapping
  public Profile profile(@AuthenticationPrincipal UserPrincipal principal) {
    return profileService.profile(principal);
  }

  @PostMapping(params = "general")
  public Profile updateProfileGeneral(@RequestBody Profile input, @AuthenticationPrincipal UserPrincipal principal) {
    Profile profile = profileService.updateProfileGeneral(input, principal);
    principal.setName(profile.getFullname());
    return profile;
  }

  @PostMapping(params = "avatar")
  public String updateAvatar(@RequestParam MultipartFile file, @AuthenticationPrincipal UserPrincipal principal) {
    return profileService.updateAvatar(file, principal);
  }

}
