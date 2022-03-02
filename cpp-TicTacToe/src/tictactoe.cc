#include <stdexcept>
#include <string>
#include <cmath>

#include "tictactoe/tictactoe.h"

namespace tictactoe {

using std::string;

/**
 * Initializes board and uses CheckBoardSize() to throw exception for an invalid
 * board.
 * @param board the String representation of the board
 */
Board::Board(const std::string& board) : board_(board) {
  CheckBoardSize();
}

/**
 * Determines the final board state by checking first if the board is even
 * reachable in a normal game of tic tac toe, and the checking for winners.
 * @return BoardState enum representing the board state
 */
auto Board::EvaluateBoard() const -> BoardState {
  if (IsReachable()) {
    switch (GetWinner()) {
      case Player::Neutral:
        return BoardState::NoWinner;
      case Player::X:
        return BoardState::Xwins;
      case Player::O:
        return BoardState::Owins;
      default:
        return BoardState::UnreachableState;
    }
  }

  return BoardState::UnreachableState;
}

/**
 * Checks the initialized board string to see if it is a valid representation
 *  of a square tic tac toe board based on if it is a perfect square length.
 *  If not, throws invalid_argument().
 */
void Board::CheckBoardSize() {
  int board_string_length = board_.length();
  int proposed_board_size = sqrt(board_string_length);
  if (proposed_board_size * proposed_board_size == board_string_length
      && proposed_board_size != 0) {
    board_size_ = proposed_board_size;
  } else {
    throw std::invalid_argument("The string provided is not a valid board.");
  }
}

/**
 * Checks if the current board can be reached via a valid game of tic tac
 *  toe where rules are followed. The rules are documented in inline comments.
 * @return true if the board is reachable, false otherwise
 */
bool Board::IsReachable() const {
  int x_count = GetPlayerMarkerCount(Player::X);
  int o_count = GetPlayerMarkerCount(Player::O);
  if (x_count != o_count && x_count != o_count + 1) {
    return false;
  }

  // Verifies that both players have not won
  bool x_won = IsWinner(Player::X);
  bool o_won = IsWinner(Player::O);
  if (x_won && o_won) {
    return false;
  }

  // Verifies that if O won, the number of O markings is equal to X markings
  if (o_won && o_count != x_count) {
    return false;
  }

  // Verifies that if X won, the number of X markings is one more than
  // the number of O markings
  if (x_won && x_count != o_count + 1) {
    return false;
  }

  return true;
}

/**
 * Gets the number of markings on the board for a particular player.
 * Simply iterates through entire board checking for the specified player's
 * markings.
 * @param player the Player enum of the player to get the markings amount for
 * @return an int number of markings
 */
int Board::GetPlayerMarkerCount(Player player) const {
  int x_count = 0;
  for (size_t idx = 0; idx < board_.length(); ++idx) {
    if (GetGameMarkerID(board_[idx]) == player) {
      ++x_count;
    }
  }

  return x_count;
}

/**
 * Gets the appropriate Player enum for a particular character marker.
 * @param marker the character on the board string
 * @return the Player enum representation of the marker
 */
auto Board::GetGameMarkerID(char marker) -> Player {
  if (marker == 'x' || marker == 'X') {
    return Player::X;
  } else if (marker == 'o' || marker == 'O') {
    return Player::O;
  } else {
    return Player::Neutral;
  }
}

/**
 * Gets the Player enum of the player which won on the game board. It assumes
 *  the game board is reachable.
 * @return the player that won, or Player::Neutral if no winner
 */
auto Board::GetWinner() const -> Player {
  if (IsWinner(Player::X)) {
    return Player::X;
  }
  if (IsWinner(Player::O)) {
    return Player::O;
  }
  return Player::Neutral;
}

/**
 * Checks if the specified Player enum won in columns, rows or diagonals
 * @param player the Player enum to check for
 * @return true if player won, false if not
 */
bool Board::IsWinner(Player player) const {
  return CheckColumnsWinner(player) || CheckRowsWinner(player)
      || CheckDiagonalsWinner(player);
}

/**
 * Checks if a particular player has won in any of the columns.
 * Iterates through the board string via column-then-row traversal.
 * @param player the Player enum to check for
 * @return true if player won in any column, false otherwise
 */
bool Board::CheckColumnsWinner(Player player) const {
  for (size_t column = 0; column < board_size_; column++) {
    size_t marker_count = 0;
    for (size_t row = 0; row < board_size_; row++) {
      if (GetGameMarkerID(board_[column + row * board_size_]) == player) {
        marker_count++;
      }
    }

    if (marker_count == board_size_) {
      return true;
    }
  }

  return false;
}

/**
 * Checks if the specified player has won in any of the rows.
 * Iterates through the board string via row-then-column traversal.
 * @param player the Player enum to check for
 * @return true if the player has won in any row, false otherwise
 */
bool Board::CheckRowsWinner(Player player) const {
  for (size_t row = 0; row < board_size_; row++) {
    size_t marker_count = 0;
    for (size_t column = 0; column < board_size_; column++) {
      if (GetGameMarkerID(board_[column + row * board_size_]) == player) {
        marker_count++;
      }
    }

    if (marker_count == board_size_) {
      return true;
    }
  }

  return false;
}

/**
 * Checks if the specified player has won on any of the diagonals.
 * Checks for either forward or backward diagonal win.
 * @param player the Player enum to check for
 * @return true if the player won on any of the diagonals
 */
bool Board::CheckDiagonalsWinner(Player player) const {
  return CheckForwardDiagonal(player) || CheckBackwardDiagonal(player);
}

/**
 * Checks if the specified player has won on the forward leaning diagonal,
 *  aka the counter diagonal. Iterates for each row and gets the column
 *  based on distance from the end of one side.
 * @param player the Player enum to check for
 * @return true if the player has won on the forward diagonal, false otherwise
 */
bool Board::CheckForwardDiagonal(Player player) const {
  size_t marker_count = 0;
  for (size_t row = 0; row < board_size_; row++) {
    Player player_marker =
        GetGameMarkerID(board_[(board_size_ - 1) + row * (board_size_ - 1)]);
    if (player_marker == player) {
      marker_count++;
    }
  }

  return marker_count == board_size_;
}

/**
 * Checks if the specified player has won on the backward leaning diagonal,
 *  aka the main diagonal. Simply iterates based on row-then-column traversal
 *  but where the row and column are the same.
 * @param player the Player enum to check for
 * @return true if the player has won on the backward diagonal, false otherwise
 */
bool Board::CheckBackwardDiagonal(Player player) const {
  size_t marker_count = 0;
  for (size_t position = 0; position < board_size_; position++) {
    Player player_marker =
        GetGameMarkerID(board_[position + position * board_size_]);
    if (player_marker == player) {
      marker_count++;
    }
  }

  return marker_count == board_size_;
}

}  // namespace tictactoe
