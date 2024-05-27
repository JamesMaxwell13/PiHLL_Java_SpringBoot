package com.sharesapp.backend.service;

public interface RequestCounterService {
  public void increment();

  public Integer getCount();
}