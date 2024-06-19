package com.cultiva.webapp.exception;

public class AlreadyExistsException extends RuntimeException {
  public AlreadyExistsException(String msg) {
    super(msg);
  }

  public AlreadyExistsException(String msg, Throwable t) {
    super(msg, t);
  }
}
