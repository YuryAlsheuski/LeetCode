package com.alsheuski.sudoku;

import java.util.ArrayList;
import java.util.List;

import static com.alsheuski.sudoku.SudokuTable.EMPTY_POSITION_SYMBOL;

public abstract class CellsContainer {
  protected final List<Cell> cells = new ArrayList<>(9);

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
