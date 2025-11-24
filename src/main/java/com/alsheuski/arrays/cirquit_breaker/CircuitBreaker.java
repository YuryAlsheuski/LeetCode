package com.alsheuski.arrays.cirquit_breaker;

import java.util.function.Supplier;

public interface CircuitBreaker {
  void onAction(Supplier<Boolean> action);
  void reset();
}
