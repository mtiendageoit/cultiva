package com.cultiva.webapp.user;


import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class RegistrationSuccessEvent extends ApplicationEvent {
  private String appUrl;
  private User user;

  public RegistrationSuccessEvent(User user, String appUrl) {
    super(user);
    this.user = user;
    this.appUrl = appUrl;
  }
}