package com.alsheuski;

import com.alsheuski.sudoku.Solver;
import com.alsheuski.sudoku.SudokuConstructor;
import com.alsheuski.sudoku.SudokuTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    SudokuConstructor constructor = new SudokuConstructor();
    SudokuTable sudokuTable = constructor.create(table);
    Solver solver = new Solver(sudokuTable);
    solver.solve();
    System.out.println(sudokuTable);
  }
}
