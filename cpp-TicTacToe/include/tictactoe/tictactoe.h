#pragma once

#include <string>

namespace tictactoe {

/**
 * This enumeration specifies the possible results of the evaluation of a
 * Tic-Tac-Toe board.
 */
enum class BoardState {
  NoWinner,
  Xwins,
  Owins,
  UnreachableState,
};

enum class Player { Neutral, X, O };

/**
 * This class can store a Tic-Tac-Toe board and evaluate its state.
 */
class Board {
 public:
  /**
   * Constructs a Board from a string consisting of 9 characters in row-major
   * order (i.e. the first three characters specify the first row).
   *
   * An 'X' or 'x' represents that the corresponding square is filled with an X,
   * an 'O' or 'o' represents that the corresponding square is filled with an O,
   * and any other character represents that the corresponding square is empty.
   *
   * This method throws a std::invalid_argument exception if the string provided
   * is not a valid board.
   */
  Board(const std::string& board);

  /**
 * Gets the board state of the proposed tic tac toe board based on the
 *  board string.
 * @return BoardState enum representing the board state
   */
  BoardState EvaluateBoard() const;

 private:
  /**
   * The string representing the state of the board.
   * Must be a perfect square length
   */
  std::string board_;

  /**
   * The length of one side of a square tic tac toe board
   */
  size_t board_size_;

  // HELPER FUNCTIONS

  /**
   * Gets the appropriate Player enum for a particular character marker.
   * @param marker the character on the board string
   * @return the Player enum representation of the marker
   */
  static Player GetGameMarkerID(char marker);

  /**
   * Checks the initialized board string to see if it is a valid representation
   *  of a square tic tac toe board. If not, throws invalid_argument().
   */
  void CheckBoardSize();

  /**
   * Checks if the current board can be reached via a valid game of tic tac
   *  toe where rules are followed.
   * @return true if the board is reachable, false otherwise
   */
  bool IsReachable() const;

  /**
   * Gets the number of markings on the board for a particular player.
   * @param player the Player enum of the player to get the markings amount for
   * @return an int number of markings
   */
  int GetPlayerMarkerCount(Player player) const;

  /**
   * Gets the Player enum of the player which won on the game board. It assumes
   *  the game board is reachable.
   * @return the player that won, or Player::Neutral if no winner
   */
  Player GetWinner() const;

  /**
   * Checks if the specified Player enum won in columns, rows or diagonals
   * @param player the Player enum to check for
   * @return true if player won, false if not
   */
  bool IsWinner(Player player) const;

  /**
   * Checks if a particular player has won in any of the columns.
   * @param player the Player enum to check for
   * @return true if player won in any column, false otherwise
   */
  bool CheckColumnsWinner(Player player) const;

  /**
   * Checks if the specified player has won in any of the rows.
   * @param player the Player enum to check for
   * @return true if the player has won in any row, false otherwise
   */
  bool CheckRowsWinner(Player player) const;

  /**
   * Checks if the specified player has won on any of the diagonals.
   * @param player the Player enum to check for
   * @return true if the player won on any of the diagonals
   */
  bool CheckDiagonalsWinner(Player player) const;

  /**
   * Checks if the specified player has won on the forward leaning diagonal,
   *  aka the counter diagonal.
   * @param player the Player enum to check for
   * @return true if the player has won on the forward diagonal, false otherwise
   */
  bool CheckForwardDiagonal(Player player) const;

  /**
   * Checks if the specified player has won on the backward leaning diagonal,
   *  aka the main diagonal.
   * @param player the Player enum to check for
   * @return true if the player has won on the backward diagonal, false otherwise
   */
  bool CheckBackwardDiagonal(Player player) const;
};

}  // namespace tictactoe