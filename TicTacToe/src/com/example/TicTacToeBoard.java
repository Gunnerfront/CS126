package com.example;

import java.lang.String;

/**
 * Takes in and evaluates a string representing a tic-tac-toe board.
 */
public class TicTacToeBoard {

  // Supposed to be a 9 character-long string depicting a 3x3 tic-tac-toe grid
  // with Xs and Os depicting player markings and other symbols depicting
  // empty spaces
  private String board;

  private int boardLength;

  // From https://www.geeksforgeeks.org/validity-of-a-given-tic-tac-toe-board-configuration/
  private static final int[][] WIN_COMBINATIONS = {
      {0, 1, 2},
      {3, 4, 5}, // Check second Row
      {6, 7, 8}, // Check third Row
      {0, 3, 6}, // Check first column
      {1, 4, 7}, // Check second Column
      {2, 5, 8}, // Check third Column
      {0, 4, 8}, // Check first Diagonal
      {2, 4, 6}  // Check second diagonal
  };

  /**
   * This method should load a string into your TicTacToeBoard class.
   * @param board The string representing the board
   */
  public TicTacToeBoard(String board) {
    this.board = board;
    this.boardLength = board.length();

    if (isInvalidBoard()) {
      throw new IllegalArgumentException("Board must be 9 characters in length.");
    }
  }

  /**
   * Checks the state of the board (unreachable, no winner, X wins, or O wins)
   * @return an enum value corresponding to the board evaluation
   */
  public Evaluation evaluate() {
    if (isReachable()) {
      switch (getWinner()) {
        case 0:
          return Evaluation.NoWinner;
        case 1:
          return Evaluation.Xwins;
        case 2:
          return Evaluation.Owins;
        default:
          return Evaluation.UnreachableState;
      }
    } else {
      return Evaluation.UnreachableState;
    }
  }

  private boolean isInvalidBoard() {
    return boardLength != 9;
  }

  private boolean isReachable() {
    // Verifies correct number of markings for each player
    int xCount = getXCount();
    int oCount = getOCount();
    if (xCount != oCount && xCount != oCount + 1) {
      return false;
    }

    // Verifies that both players have not won
    boolean xWon = xIsWinner();
    boolean oWon = oIsWinner();
    if (xWon && oWon) {
      return false;
    }

    // Verifies that if O won, the number of O markings is equal to X markings
    if (oWon && oCount != xCount) {
      return false;
    }

    // Verifies that if X won, the number of X markings is one more than
    // the number of O markings
    if (xWon && xCount != oCount + 1) {
      return false;
    }

    return true;
  }

  private int getWinner() {
    assert(isReachable());

    if (xIsWinner()) {
      return 1;
    } else if (oIsWinner()) {
      return 2;
    } else {
      return 0;
    }

  }

  private int getXCount() {
    int xCount = 0;
    for (int idx = 0; idx < boardLength; ++idx) {
      if (getGameMarkerID(board.charAt(idx)) == 1) {
        ++xCount;
      }
    }

    return xCount;
  }

  private int getOCount() {
    int oCount = 0;
    for (int idx = 0; idx < boardLength; ++idx) {
      if (getGameMarkerID(board.charAt(idx)) == 2) {
        ++oCount;
      }
    }

    return oCount;
  }

  private boolean xIsWinner() {
    // Checks each combination for 3-in-a-row
    for (int[] winCombo : WIN_COMBINATIONS) {
      if (getGameMarkerID(board.charAt(winCombo[0])) == 1
          && getGameMarkerID(board.charAt(winCombo[1])) == 1
          && getGameMarkerID(board.charAt(winCombo[2])) == 1) {
        return true;
      }
    }

    return false;
  }

  private boolean oIsWinner() {
    // Checks each combination for 3-in-a-row
    for (int[] winCombo : WIN_COMBINATIONS) {
      if (getGameMarkerID(board.charAt(winCombo[0])) == 2
          && getGameMarkerID(board.charAt(winCombo[1])) == 2
          && getGameMarkerID(board.charAt(winCombo[2])) == 2) {
        return true;
      }
    }

    return false;
  }

  private int getGameMarkerID(char symbol) {
    if (symbol == 'x' || symbol == 'X') {
      return 1;
    } else if (symbol == 'o' || symbol == 'O') {
      return 2;
    } else {
      return 0;
    }
  }
}
