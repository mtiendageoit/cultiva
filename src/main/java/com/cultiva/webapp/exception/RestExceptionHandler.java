package com.cultiva.webapp.exception;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BaseException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleBaseException(BaseException exception,
      WebRequest request) {
    String error = exception.getMessage();
    log.error(error, exception);
    return buildErrorResponse(exception, exception.getCode(), error, null, HttpStatus.BAD_REQUEST,
        request);
  }

  private ResponseEntity<Object> buildErrorResponse(Exception exception,
      String code,
      String message,
      Map<String, Object> data,
      HttpStatus httpStatus,
      WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setCode(code);
    errorResponse.setData(data);
    errorResponse.setMessage(message);
    errorResponse.setStatus(httpStatus.value());

    return ResponseEntity.status(httpStatus).body(errorResponse);
  }
}
