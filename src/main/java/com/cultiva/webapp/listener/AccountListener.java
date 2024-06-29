package com.cultiva.webapp.listener;

import java.util.Date;

import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.cultiva.webapp.config.AppConfig;
import com.cultiva.webapp.user.*;
import com.cultiva.webapp.utils.RandomCodeGenerator;

import jakarta.mail.internet.*;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AccountListener {
  private AppConfig config;
  private final UserService userService;
  private final JavaMailSender mailSender;

  @Async
  @EventListener
  public void onRegistrationSuccessEvent(RegistrationSuccessEvent event) throws Exception {
    User user = event.getUser();
    String code = RandomCodeGenerator.generateUUIDCode();
    String link = event.getAppUrl() + "/verify?token=" + code;

    String subject = "Bienvenido a Cultiva";

    String message = "Estimado usuario,\n\n";
    message += "¡Bienvenido a Cultiva!\n\n";
    message += "Para acceder a nuestros servicios, primero debes verificar tu correo electrónico visitando el siguiente enlace: \n";
    message += link;
    message += "\n\nEn Cultiva, nos esforzamos por ofrecerte los mejores servicios de análisis para la agricultura.";
    message += "\nEsperamos que disfrutes de nuestra plataforma.";
    message += "\n\nAtentamente,";
    message += "\nEquipo Cultiva.";
    sendMail(user.getEmail(), subject, message);

    user.setConfirmationToken(code);
    user.setConfirmationTokenCreatedAt(new Date());
    userService.save(user);
  }

  @Async
  @EventListener
  public void onResetPasswordEvent(ResetPasswordEvent event) throws Exception {
    User user = event.getUser();
    String code = RandomCodeGenerator.generateUUIDCode();
    String link = event.getAppUrl() + "/change-password?token=" + code;

    String subject = "Solicitud de Recuperación de Contraseña";

    String message = "Estimado usuario,\n\n";
    message += "Hemos recibido una solicitud para recuperar la contraseña de tu cuenta en Cultiva.\n\n";
    message += "Si fuiste tú quien realizó esta solicitud, por favor, visita el siguiente enlace para restablecer tu contraseña: \n";
    message += link;
    message += "\n\nSi NO solicitaste esta recuperación de contraseña, por favor, ignora este mensaje.";
    message += "\n\nAtentamente,";
    message += "\nEquipo Cultiva.";

    sendMail(user.getEmail(), subject, message);

    user.setResetPasswordToken(code);
    user.setResetPasswordTokenCreatedAt(new Date());
    userService.save(user);
  }

  private void sendMail(String to, String subject, String text) throws Exception {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    message.setFrom(new InternetAddress(config.getAppMailSender(), "Cultiva"));
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    mailSender.send(mimeMessage);
  }

}
