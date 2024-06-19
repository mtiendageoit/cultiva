package com.cultiva.webapp.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
  private String code;

  public BaseException() {
    super();
  }

  public BaseException(String message) {
    super(message);
  }

  public BaseException(String message, Throwable cause) {
    super(message, cause);
  }

  public BaseException(String code, String message) {
    this(message);
    this.code = code;
  }

  public BaseException(String code, String message, Throwable cause) {
    this(message, cause);
    this.code = code;
  }
}
