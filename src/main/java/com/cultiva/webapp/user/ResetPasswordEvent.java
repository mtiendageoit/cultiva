package com.cultiva.webapp.user;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class ResetPasswordEvent extends ApplicationEvent {
  private User user;
  private String appUrl;

  public ResetPasswordEvent(User user, String appUrl) {
    super(user);
    this.user = user;
    this.appUrl = appUrl;
  }
}