package com.alsheuski.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.alsheuski.sudoku.SudokuTable.EMPTY_POSITION_SYMBOL;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;

public class Solver {

  private final SudokuTable table;

  public Solver(SudokuTable table) {
    this.table = table;
  }

  public void solve() {

    while (true) {

      List<Square> squares =
          table.getSquares().stream()
              .filter(i -> i.getEmptyCellsCount() > 0)
              .sorted(comparingLong(CellsContainer::getEmptyCellsCount))
              .collect(toList());

      if (squares.isEmpty()) {
        return;
      }

      for (Square square : squares) {
        List<String> val = findNotExistedElementsFromPivot(square);
        solveSquare(square, val);
      }
    }
  }

  private List<String> findNotExistedElementsFromPivot(Square square) {

    List<String> elements = new ArrayList<>((int) square.getEmptyCellsCount());
    for (int i = 1; i <= square.getCells().size(); i++) {
      String cellValue = String.valueOf(i);
      if (square.getCells().stream().noneMatch(l -> l.getValue().equals(cellValue))) {
        elements.add(cellValue);
      }
    }
    return elements;
  }

  private void solveSquare(Square square, List<String> newCellsValues) {
    int limit = getIterationCount(newCellsValues);
    for (int k = 0; k < limit; k++) {
      List<Cell> emptyCells =
          square.getCells().stream()
              .filter(i -> i.getValue().equals(EMPTY_POSITION_SYMBOL))
              .collect(toList());

      if (emptyCells.isEmpty()) {
        return;
      }

      for (Cell cell : emptyCells) {

        List<String> existedValues =
            Stream.concat(table.getRows().stream(), table.getColumns().stream())
                .filter(i -> i.getCells().contains(cell))
                .flatMap(i -> i.getCells().stream())
                .map(Cell::getValue)
                .collect(toList());

        List<String> appropriateValues =
            newCellsValues.stream().filter(i -> !existedValues.contains(i)).collect(toList());

        if (appropriateValues.size() == 1) {
          String newValue = appropriateValues.stream().findFirst().get();
          cell.setValue(newValue);
          newCellsValues.remove(newValue);
        }
      }
    }
  }

  private int getIterationCount(List<String> newCellsValues) {
    int result = 0;
    for (int i = 1; i <= newCellsValues.size(); i++) {
      result = result + i;
    }
    return result * newCellsValues.size();
  }
}
