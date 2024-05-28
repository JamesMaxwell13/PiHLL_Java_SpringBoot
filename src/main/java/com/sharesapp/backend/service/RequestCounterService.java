package com.sharesapp.backend.service;

public interface RequestCounterService {
  Integer increment();

  Integer getCount();
}