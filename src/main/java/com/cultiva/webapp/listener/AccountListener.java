package com.cultiva.webapp.listener;


import java.util.Date;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.cultiva.webapp.user.*;
import com.cultiva.webapp.utils.RandomCodeGenerator;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AccountListener {

  private final UserService userService;
  private final JavaMailSender mailSender;

  @Async
  @EventListener
  public void onRegistrationSuccessEvent(RegistrationSuccessEvent event) {
    User user = event.getUser();
    String code = RandomCodeGenerator.generateUUIDCode();
    String link = event.getAppUrl() + "/verify?token=" + code;

    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(user.getEmail());
    email.setSubject("Verificación de cuenta");
    email.setText("Este es el link para verificar su cuenta: " + link);
    mailSender.send(email);

    user.setConfirmationToken(code);
    user.setConfirmationTokenCreatedAt(new Date());
    userService.save(user);
  }

  @Async
  @EventListener
  public void onResetPasswordEvent(ResetPasswordEvent event) {
    User user = event.getUser();
    String code = RandomCodeGenerator.generateUUIDCode();
    String link = event.getAppUrl() + "/change-password?token=" + code;

    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(user.getEmail());
    email.setSubject("Password Reset");
    email.setText("Este es el link para restablecer su contarseña: " + link);
    mailSender.send(email);

    user.setResetPasswordToken(code);
    user.setResetPasswordTokenCreatedAt(new Date());
    userService.save(user);
  }

}
