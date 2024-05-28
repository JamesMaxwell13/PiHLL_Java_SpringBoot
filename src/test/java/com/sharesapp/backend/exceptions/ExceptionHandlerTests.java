package com.sharesapp.backend.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerTests {

  @InjectMocks
  private ExceptionHandlerController exceptionHandler;

  @Test
  void testHandleBadRequestException() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    WebRequest webRequest = new ServletWebRequest(request);
    ResponseEntity<ExceptionParameter> result =
        exceptionHandler.badRequest(new BadRequestException("Test"), webRequest);
    assertEquals("Test", Objects.requireNonNull(result.getBody()).getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
  }

  @Test
  void testHandlerNotFoundException() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    WebRequest webRequest = new ServletWebRequest(request);
    ResponseEntity<ExceptionParameter> result =
        exceptionHandler.notFound(new NotFoundException("Test"), webRequest);
    assertEquals("Test", Objects.requireNonNull(result.getBody()).getMessage());
    assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
  }

  @Test
  void testHandlerNotFoundExceptionId() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    WebRequest webRequest = new ServletWebRequest(request);
    ResponseEntity<ExceptionParameter> result =
        exceptionHandler.notFound(new NotFoundException("Test", 1L), webRequest);
    assertEquals("Test" + 1L, Objects.requireNonNull(result.getBody()).getMessage());
    assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
  }

  @Test
  void testHandlerInternalServiceException() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    WebRequest webRequest = new ServletWebRequest(request);
    ResponseEntity<ExceptionParameter> result =
        exceptionHandler.internalServiceException(new Exception("Test"), webRequest);
    assertEquals("Test", Objects.requireNonNull(result.getBody()).getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
  }
}
