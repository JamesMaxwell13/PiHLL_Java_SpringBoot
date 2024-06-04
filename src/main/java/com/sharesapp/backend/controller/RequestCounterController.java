package com.sharesapp.backend.controller;

import com.sharesapp.backend.service.RequestCounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/counter")
public class RequestCounterController {
  private final RequestCounterService requestCounterService;

  public RequestCounterController(RequestCounterService requestCounterService) {
    this.requestCounterService = requestCounterService;
  }

  @GetMapping
  public ResponseEntity<Integer> getRequestCount() {
    return ResponseEntity.ok(requestCounterService.getCount());
  }
}
