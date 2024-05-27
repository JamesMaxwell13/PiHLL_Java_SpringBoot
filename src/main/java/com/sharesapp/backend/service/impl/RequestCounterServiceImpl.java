package com.sharesapp.backend.service.impl;

import com.sharesapp.backend.aspect.annotation.Logging;
import com.sharesapp.backend.service.RequestCounterService;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class RequestCounterServiceImpl implements RequestCounterService {
  private final AtomicInteger count = new AtomicInteger(0);

  @Logging
  @Override
  public void increment() {
    count.incrementAndGet();
  }

  @Logging
  @Override
  public Integer getCount() {
    return count.get();
  }
}