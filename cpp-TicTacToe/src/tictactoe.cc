#pragma clang diagnostic push
#pragma ide diagnostic ignored "modernize-use-trailing-return-type"
#include <stdexcept>
#include <string>
#include <cmath>

#include "tictactoe/tictactoe.h"

namespace tictactoe {

using std::string;

Board::Board(const string& board) : board_(board) {
  CheckBoardSize();
}

/**
 * Gets the board state of the proposed tic tac toe board based on the
 * board string.
 * @return BoardState enum representing the board state
 */
BoardState Board::EvaluateBoard() const {
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
 * of a square tic tac toe board. If not, throws invalid_argument().
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


bool Board::IsReachable() const {
  int x_count = GetPlayerMarkerCount(Player::X);
  int o_count = GetPlayerMarkerCount(Player::O);
  if (x_count != o_count && x_count != o_count + 1) {
    return false;
  }

  // Verifies that both players have not won
  bool x_won = XIsWinner();
  bool o_won = OIsWinner();
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

int Board::GetPlayerMarkerCount(Player player) const {
  int x_count = 0;
  for (size_t idx = 0; idx < board_.length(); ++idx) {
    if (GetGameMarkerID(board_[idx]) == player) {
      ++x_count;
    }
  }

  return x_count;
}

Player Board::GetGameMarkerID(char marker) {
  if (marker == 'x' || marker == 'X') {
    return Player::X;
  } else if (marker == 'o' || marker == 'O') {
    return Player::O;
  } else {
    return Player::Neutral;
  }
}

Player Board::GetWinner() const {
  if (XIsWinner()) {
    return Player::X;
  }
  if (OIsWinner()) {
    return Player::O;
  }
  return Player::Neutral;
}

bool Board::XIsWinner() const {
  return CheckColumnsWinner(Player::X) || CheckRowsWinner(Player::X)
      || CheckDiagonalsWinner(Player::X);
}

bool Board::OIsWinner() const {
  return CheckColumnsWinner(Player::O) || CheckRowsWinner(Player::O)
         || CheckDiagonalsWinner(Player::O);
}

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

bool Board::CheckDiagonalsWinner(Player player) const {
  // Check forward slash diagonal
  size_t marker_count = 0;
  for (size_t position = 0; position < board_size_; position++) {
    Player player_marker =
        GetGameMarkerID(board_[position + position * board_size_]);
    if (player_marker == player) {
      marker_count++;
    }
  }

  if (marker_count == board_size_) {
    return true;
  }

  // Check backslash diagonal
  marker_count = 0;
  for (size_t row = 0; row < board_size_; row++) {
    Player player_marker =
        GetGameMarkerID(board_[(board_size_ - 1) + row * (board_size_ - 1)]);
    if (player_marker == player) {
      marker_count++;
    }
  }

  if (marker_count == board_size_) {
    return true;
  }

  return false;

  //return CheckForwardDiagonal(player) || CheckBackwardDiagonal(player);
}

}  // namespace tictactoe
