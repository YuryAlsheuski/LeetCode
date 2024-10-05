package com.alsheuski.sudoku;

import com.alsheuski.sudoku.domain.SudokuTable;

import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    solveSudoku();
  }

  // https://leetcode.com/problems/sudoku-solver/
  private static void solveSudoku() {
    String[][] table = {
      {".", ".", ".", "9", ".", ".", "7", "2", "8"},
      {"2", "7", "8", ".", ".", "3", ".", "1", "."},
      {".", "9", ".", ".", ".", ".", "6", "4", "."},
      {".", "5", ".", ".", "6", ".", "2", ".", "."},
      {".", ".", "6", ".", ".", ".", "3", ".", "."},
      {".", "1", ".", ".", "5", ".", ".", ".", "."},
      {"1", ".", ".", "7", ".", "6", ".", "3", "4"},
      {".", ".", ".", "5", ".", "4", ".", ".", "."},
      {"7", ".", "9", "1", ".", ".", "8", ".", "5"}
    };

    var t = new SudokuConstructor(table).create();
    Solver solver = new Solver(t,buildLinePattern(t.getSquares().size()));
    SudokuTable sudokuTable = solver.solve();
    System.out.println(sudokuTable);
  }

  private static List<String> buildLinePattern(int elementsCount) {
    List<String> elements = new ArrayList<>(elementsCount);
    for (int i = 1; i <= elementsCount; i++) {
      elements.add(String.valueOf(i));
    }
    return elements;
  }
}
