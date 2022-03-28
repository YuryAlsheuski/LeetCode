package com.alsheuski.sudoku;

import com.alsheuski.sudoku.domain.Cell;
import com.alsheuski.sudoku.domain.Line;
import com.alsheuski.sudoku.domain.Square;
import com.alsheuski.sudoku.domain.SudokuTable;

import java.util.List;
import java.util.Optional;

public class SudokuConstructor {

  private final String[][] table;

  public SudokuConstructor(String[][] table) {
    this.table = table;
  }

  public SudokuTable create() {

    SudokuTable sudokuTable = new SudokuTable();

    for (int i = 0; i < table.length; i++) {
      String[] line = table[i];
      sudokuTable.getRows().add(i, new Line());

      for (int j = 0; j < line.length; j++) {
        fillData(sudokuTable, j, i, line[j]);
      }
    }

    return sudokuTable;
  }

  private void fillData(SudokuTable sudokuTable, int x, int y, String value) {
    Cell cell = new Cell(x, y, value);

    fillColumn(sudokuTable.getColumns(), cell);
    fillSquare(sudokuTable.getSquares(), cell);

    sudokuTable.getRows().get(y).addCell(cell);
  }

  private void fillColumn(List<Line> columns, Cell cell) {
    Line column = getColumn(columns, cell);
    column.addCell(cell);
  }

  private Line getColumn(List<Line> columns, Cell cell) {
    if (columns.size() < cell.getX() + 1) {
      Line column = new Line();
      columns.add(column);
      return column;
    }
    return columns.get(cell.getX());
  }

  private void fillSquare(List<Square> squares, Cell cell) {
    Optional<Square> square = squares.stream().filter(s -> s.containsCell(cell)).findFirst();

    if (square.isPresent()) {
      square.get().addCell(cell);
    } else {
      int x = cell.getX();
      int y = cell.getY();
      Square newSquare = new Square(x, x + 2, y, y + 2);
      newSquare.addCell(cell);
      squares.add(newSquare);
    }
  }
}
