package com.sharesapp.backend.exceptions;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionHandlerController {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ExceptionParameter> notFound(NotFoundException exception,
                                                     WebRequest request) {
    ExceptionParameter details = new ExceptionParameter(new Date(), exception.getMessage(),
        request.getDescription(false));
    return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ExceptionParameter> badRequest(BadRequestException exception,
                                                       WebRequest request) {
    ExceptionParameter details = new ExceptionParameter(new Date(), exception.getMessage(),
        request.getDescription(false));
    return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionParameter> internalServiceException(Exception exception,
                                                                     WebRequest request) {
    ExceptionParameter details = new ExceptionParameter(new Date(), exception.getMessage(),
        request.getDescription(false));
    return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}