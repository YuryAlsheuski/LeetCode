package com.alsheuski.sudoku.domain;

import java.util.ArrayList;
import java.util.List;

import static com.alsheuski.sudoku.domain.SudokuTable.DEFAULT_CAPACITY;
import static com.alsheuski.sudoku.domain.SudokuTable.EMPTY_POSITION_SYMBOL;

public abstract class CellsContainer {
  protected final List<Cell> cells = new ArrayList<>(DEFAULT_CAPACITY);

  public long getEmptyCellsCount() {
    return cells.stream().filter(i -> i.getValue().equals(EMPTY_POSITION_SYMBOL)).count();
  }

  public void addCell(Cell cell) {
    cells.add(cell);
  }

  public List<Cell> getCells() {
    return cells;
  }
}
