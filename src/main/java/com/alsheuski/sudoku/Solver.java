package com.alsheuski.sudoku;

import com.alsheuski.sudoku.domain.Cell;
import com.alsheuski.sudoku.domain.CellsContainer;
import com.alsheuski.sudoku.domain.Square;
import com.alsheuski.sudoku.domain.SudokuTable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.alsheuski.sudoku.domain.SudokuTable.EMPTY_POSITION_SYMBOL;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;

public class Solver {

  private final SudokuTable table;
  private final List<String> solvedLinePattern;

  public Solver(String[][] table) {
    this.table = new SudokuConstructor(table).create();
    solvedLinePattern = buildLinePattern(this.table.getSquares().size());
  }

  public SudokuTable solve() {

    while (true) {

      List<Square> squares =
          table.getSquares().stream()
              .filter(i -> i.getEmptyCellsCount() > 0)
              .sorted(comparingLong(CellsContainer::getEmptyCellsCount))
              .collect(toList());

      if (squares.isEmpty()) {
        return table;
      }

      for (Square square : squares) {
        List<String> val = findNotExistedElementsFromPivot(square);
        solveSquare(square, val);
      }
    }
  }

  private List<String> findNotExistedElementsFromPivot(Square square) {
    return solvedLinePattern.stream()
        .filter(i -> square.getCells().stream().noneMatch(k -> k.getValue().equals(i)))
        .collect(toList());
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

  private List<String> buildLinePattern(int elementsCount) {
    List<String> elements = new ArrayList<>(elementsCount);
    for (int i = 1; i <= elementsCount; i++) {
      elements.add(String.valueOf(i));
    }
    return elements;
  }
}
