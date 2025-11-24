package com.alsheuski.arrays.cirquit_breaker;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class BaseCircuitBreaker implements CircuitBreaker {

  private final int timeoutMS;
  private final int failedActionsThreshold;
  private int failedActions;
  private final AtomicBoolean isClosed;
  private ScheduledExecutorService executor;

  public BaseCircuitBreaker(int timeoutMS, int failedActionsThreshold) {
    this.timeoutMS = timeoutMS;
    this.failedActionsThreshold = failedActionsThreshold;
    failedActions = 0;
    isClosed = new AtomicBoolean(false);
  }

  @Override
  public synchronized void onAction(Supplier<Boolean> action) {
    if (isClosed.get()) {
      return;
    }
    if (!action.get()) {
      failedActions = failedActions + 1;
    }
    if (failedActions > failedActionsThreshold) {
      isClosed.set(true);
      executor.schedule(() -> isClosed.set(false), timeoutMS, TimeUnit.MILLISECONDS);
    }
  }

  @Override
  public synchronized void reset() {}
}
