package com.alsheuski.sudoku.domain;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class SudokuTable {

  public static final String EMPTY_POSITION_SYMBOL = ".";
  public static final int DEFAULT_CAPACITY = 9;

  private final List<Line> rows = new ArrayList<>(DEFAULT_CAPACITY);
  private final List<Line> columns = new ArrayList<>(DEFAULT_CAPACITY);
  private final List<Square> squares = new ArrayList<>(DEFAULT_CAPACITY);

  public List<Line> getRows() {
    return rows;
  }

  public List<Line> getColumns() {
    return columns;
  }

  public List<Square> getSquares() {
    return squares;
  }

  @Override
  public String toString() {
    return rows.stream().map(Object::toString).collect(joining("\n"));
  }
}
