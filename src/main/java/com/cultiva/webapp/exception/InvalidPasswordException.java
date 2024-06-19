package com.cultiva.webapp.exception;

public class InvalidPasswordException extends BaseException {
  public InvalidPasswordException(String code, String message) {
    super(code, message);
  }
}