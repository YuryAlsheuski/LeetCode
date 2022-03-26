package com.alsheuski.sudoku;

import java.util.Objects;

public class Cell {
  private final int x;
  private final int y;
  private String value;

  public Cell(int x, int y, String value) {
    this.x = x;
    this.y = y;
    this.value = value;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Cell cell = (Cell) o;
    return x == cell.x && y == cell.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return value;
  }
}
