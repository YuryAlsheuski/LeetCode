package com.alsheuski.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SudokuTable {

  public static final String EMPTY_POSITION_SYMBOL = ".";

  private final List<Line> rows = new ArrayList<>(9);
  private final List<Line> columns = new ArrayList<>(9);
  private final List<Square> squares = new ArrayList<>(9);

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
    return rows.stream().map(Object::toString).collect(Collectors.joining("\n"));
  }
}
