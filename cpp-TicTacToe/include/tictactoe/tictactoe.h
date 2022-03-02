#pragma clang diagnostic push
#pragma ide diagnostic ignored "modernize-use-nodiscard"
#pragma ide diagnostic ignored "modernize-use-trailing-return-type"
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

enum class Player {
  Neutral,
  X,
  O
};

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
   * Evaluates the state of the board.
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

    // Helper functions
    static Player GetGameMarkerID(char marker);
    void CheckBoardSize();
    bool IsReachable() const;
    int GetPlayerMarkerCount(Player) const;
    Player GetWinner() const;
    bool XIsWinner() const;
    bool OIsWinner() const;
    bool CheckColumnsWinner(Player player) const;
    bool CheckRowsWinner(Player player) const;
    bool CheckDiagonalsWinner(Player player) const;
};

}  // namespace tictactoe

#pragma clang diagnostic pop