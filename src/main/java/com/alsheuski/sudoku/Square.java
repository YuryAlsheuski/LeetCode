package com.alsheuski.sudoku;

public class Square extends CellsContainer {

  private final int xMin;
  private final int xMax;
  private final int yMin;
  private final int yMax;

  public Square(int xMin, int xMax, int yMin, int yMax) {
    this.xMin = xMin;
    this.xMax = xMax;
    this.yMin = yMin;
    this.yMax = yMax;
  }

  public boolean containsCell(Cell cell) {
    return xMin <= cell.getX() && xMax >= cell.getX() && yMin <= cell.getY() && yMax >= cell.getY();
  }
}
