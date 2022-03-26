package com.alsheuski.sudoku;

import java.util.List;
import java.util.Optional;

public class SudokuConstructor {

  public SudokuTable create(String[][] table) {

    SudokuTable sudokuTable = new SudokuTable();

    for (int i = 0; i < table.length; i++) {
      String[] line = table[i];
      List<Line> rows = sudokuTable.getRows();
      rows.add(i, new Line());

      for (int j = 0; j < line.length; j++) {
        Cell cell = new Cell(j, i, line[j]);

        fillColumn(sudokuTable.getColumns(), cell);

        fillSquare(sudokuTable.getSquares(), cell);

        rows.get(i).addCell(cell);
      }
    }

    return sudokuTable;
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
