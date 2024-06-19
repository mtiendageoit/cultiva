package com.cultiva.webapp.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import com.cultiva.webapp.user.UserService;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {
  private final UserService userService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests((requests) -> requests
            .requestMatchers("/css/**", "/js/**", "/vendor/**").permitAll()
            .requestMatchers("/oauth/**", "/signup/**", "/verify/**").permitAll()
            .requestMatchers("/password-reset/**", "/password-reset-success/**", "/password-reset-error/**").permitAll()
            .requestMatchers("/change-password/**", "/change-password-success/**", "/change-password-error/**")
            .permitAll()
            .anyRequest().authenticated())
        .formLogin((form) -> form.loginPage("/login").permitAll())
        .logout((logout) -> logout.logoutSuccessUrl("/login").permitAll())

        .oauth2Login((oauth2) -> oauth2.loginPage("/login")
            .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService())));

    return http.build();
  }

  private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
    final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    return (userRequest) -> {
      OAuth2User oidcUser = delegate.loadUser(userRequest);
      String registrationId = userRequest.getClientRegistration().getRegistrationId();
      UserPrincipal principal = userService.userPrincipalFromOAuth2Login(registrationId, oidcUser.getAttributes());
      return principal;
    };
  }

}
