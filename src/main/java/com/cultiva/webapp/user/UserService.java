package com.cultiva.webapp.user;

import java.time.Duration;
import java.util.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cultiva.webapp.exception.*;
import com.cultiva.webapp.security.*;
import com.cultiva.webapp.security.oauth2.OAuth2UserInfoFactory;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
  private static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;

  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher eventPublisher;

  public UserPrincipal userPrincipalFromOAuth2Login(String registrationId, Map<String, Object> atributes) {
    UserPrincipal principal = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, atributes);
    User user = registerUserIfNotExists(principal);
    principal.setId(user.getId());
    return principal;
  }

  private User registerUserIfNotExists(UserPrincipal principal) {
    Optional<User> opt = repository.findByEmail(principal.getEmail());

    if (opt.isPresent())
      return opt.get();

    User user = User.builder()
        .fullname(principal.getName())
        .email(principal.getEmail())
        .avatar(principal.getAvatar())
        .authProvider(principal.getProvider())
        .enabled(true)
        .createdAt(new Date())
        .build();

    return repository.save(user);
  }

  public User registerByEmail(RegisterUserDto input, String appUrl) {
    boolean exists = repository.existsByEmail(input.getEmail());
    if (exists) {
      throw new AlreadyExistsException("El email ya existe");
    }

    User user = User.builder()
        .fullname(input.getFullname())
        .email(input.getEmail())
        .password(passwordEncoder.encode(input.getPassword()))
        .authProvider(AuthProvider.database)
        .enabled(false)
        .createdAt(new Date())
        .build();

    user = repository.save(user);
    eventPublisher.publishEvent(new RegistrationSuccessEvent(user, appUrl));
    return user;
  }

  public User save(User user) {
    return repository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String email) {
    User user = repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found"));

    List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(UserRole.USER.name()));
    UserPrincipal principal = new UserPrincipal(user.getId(), user.getFullname(), email, user.getPassword(),
        authorities, user.isEnabled());

    return principal;
  }

  public void resetPassword(String email, String appUrl) {
    Optional<User> user = repository.findByEmail(email);
    boolean isPasswordResetUser = user.isPresent() && user.get().getAuthProvider() == AuthProvider.database;
    if (isPasswordResetUser) {
      eventPublisher.publishEvent(new ResetPasswordEvent(user.get(), appUrl));
    }
  }

  public boolean isResetPasswordTokenValid(String token) {
    Optional<User> user = repository.findByResetPasswordToken(token);

    if (user.isPresent()) {
      Date createdAt = user.get().getResetPasswordTokenCreatedAt();
      Duration diff = Duration.between(createdAt.toInstant(), new Date().toInstant());
      boolean expired = diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
      return !expired;
    }

    return false;
  }

  public void changePassword(String token, String password) {
    User user = repository.findByResetPasswordToken(token).orElseThrow(() -> new TokenNotFoundException());

    Date createdAt = user.getResetPasswordTokenCreatedAt();
    Duration diff = Duration.between(createdAt.toInstant(), new Date().toInstant());
    boolean expired = diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;

    if (expired) {
      throw new TokenExpiredException();
    }

    user.setPassword(passwordEncoder.encode(password));
    user.setResetPasswordToken(null);
    user.setResetPasswordTokenCreatedAt(null);
    this.save(user);
  }

  public void verifyRegistration(String token) {
    User user = repository.findByConfirmationToken(token).orElseThrow(() -> new TokenNotFoundException());

    Date createdAt = user.getConfirmationTokenCreatedAt();
    Duration diff = Duration.between(createdAt.toInstant(), new Date().toInstant());
    boolean expired = diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;

    if (expired) {
      throw new TokenExpiredException();
    }

    user.setEnabled(true);
    user.setConfirmationToken(null);
    user.setConfirmationTokenCreatedAt(null);
    this.save(user);
  }
}
