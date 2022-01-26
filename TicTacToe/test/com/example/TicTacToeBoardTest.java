package com.example;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TicTacToeBoardTest {
  @Test
  public void testValidBoardNoWinner() {
    TicTacToeBoard board = new TicTacToeBoard("O...X.X..");
    assertEquals(Evaluation.NoWinner, board.evaluate());
  }

  @Test
  public void testValidBoardXWinnerRow() {
    TicTacToeBoard board = new TicTacToeBoard("XXX.O.O..");
    assertEquals(Evaluation.Xwins, board.evaluate());
  }

  @Test
  public void testValidBoardXWinnerColumn() {
    TicTacToeBoard board = new TicTacToeBoard("X.OXO.X..");
    assertEquals(Evaluation.Xwins, board.evaluate());
  }

  @Test
  public void testValidBoardXWinnerDiagonal() {
    TicTacToeBoard board = new TicTacToeBoard("XOO.X...X");
    assertEquals(Evaluation.Xwins, board.evaluate());
  }

  @Test
  public void testValidBoardOWinnerRow() {
    TicTacToeBoard board = new TicTacToeBoard("OOO.X.XX.");
    assertEquals(Evaluation.Owins, board.evaluate());
  }

  @Test
  public void testValidBoardOWinnerColumn() {
    TicTacToeBoard board = new TicTacToeBoard("XO..O.XOX");
    assertEquals(Evaluation.Owins, board.evaluate());
  }

  @Test
  public void testValidBoardOWinnerDiagonal() {
    TicTacToeBoard board = new TicTacToeBoard("X.O.O.OXX");
    assertEquals(Evaluation.Owins, board.evaluate());
  }

  @Test
  public void testValidBoardUnreachable_Imbalance() {
    TicTacToeBoard board = new TicTacToeBoard("X.O.X.OXX");
    assertEquals(Evaluation.UnreachableState, board.evaluate());
  }

  @Test
  public void testValidBoardUnreachable_TwoWinners() {
    TicTacToeBoard board = new TicTacToeBoard("XXX...OOO");
    assertEquals(Evaluation.UnreachableState, board.evaluate());
  }

  @Test
  public void testInvalidBoard() {
    boolean exceptionThrown = false;
    try {
      TicTacToeBoard board = new TicTacToeBoard("XXXoo");
    } catch (IllegalArgumentException e) {
      exceptionThrown = true;
    }

    assertEquals(exceptionThrown, true);
  }
}
