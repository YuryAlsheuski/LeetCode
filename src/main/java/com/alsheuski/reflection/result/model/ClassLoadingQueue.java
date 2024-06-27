package com.alsheuski.reflection.result.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ClassLoadingQueue {

  private final Map<String, List<Consumer<MetaClass>>> nextLevelQueue;
  private final Predicate<String> classPathFilter;

  public ClassLoadingQueue(Predicate<String> classPathFilter) {
    this.classPathFilter = classPathFilter;
    nextLevelQueue = new HashMap<>();
  }

  public void add(String className, Consumer<MetaClass> loadAction) {
    if (!classPathFilter.test(className)) {
      return;
    }
    nextLevelQueue.computeIfAbsent(className, k -> new ArrayList<>()).add(loadAction);
  }

  public boolean isEmpty() {
    return nextLevelQueue.isEmpty();
  }

  public Set<String> getClasses() {
    return nextLevelQueue.keySet();
  }

  public Set<Entry<String, List<Consumer<MetaClass>>>> getEntries() {
    return nextLevelQueue.entrySet();
  }
}
