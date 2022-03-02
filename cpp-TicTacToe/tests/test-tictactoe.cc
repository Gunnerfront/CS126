#include <string>

#include <catch2/catch.hpp>
#include <tictactoe/tictactoe.h>

using tictactoe::Board;
using tictactoe::BoardState;

TEST_CASE("Invalid string provided to constructor") {
  SECTION("String is too short") {
    REQUIRE_THROWS_AS(Board("xxooo"), std::invalid_argument);
  }

  SECTION("String is too long") {
    REQUIRE_THROWS_AS(Board("xxooo---oox"), std::invalid_argument);
  }

  SECTION("String is empty") {
    REQUIRE_THROWS_AS(Board(""), std::invalid_argument);
  }

  SECTION("String is one character off") {
    REQUIRE_THROWS_AS(Board("xxooo-xo"), std::invalid_argument);
  }
}

TEST_CASE("Boards with no winner") {
  SECTION("Full board with no winner") {
    REQUIRE(Board("xxoooxxxo").EvaluateBoard() == BoardState::NoWinner);
  }

  SECTION("Empty board with no winner") {
    REQUIRE(Board("---mmm---").EvaluateBoard() == BoardState::NoWinner);
  }

  SECTION("Partially filled board with no winner") {
    REQUIRE(Board("ox-mmm-x-").EvaluateBoard() == BoardState::NoWinner);
  }
}

TEST_CASE("Boards where X wins") {
  SECTION("Full board where X wins") {
    REQUIRE(Board("xxxooxxoo").EvaluateBoard() == BoardState::Xwins);
  }

  SECTION("X wins diagonally") {
    REQUIRE(Board("xoo-x---x").EvaluateBoard() == BoardState::Xwins);
  }

  SECTION("X wins horizontally") {
    REQUIRE(Board("---xxxoo-").EvaluateBoard() == BoardState::Xwins);
  }

  SECTION("X wins vertically") {
    REQUIRE(Board("ox-ox--x-").EvaluateBoard() == BoardState::Xwins);
  }
}

TEST_CASE("Boards where O wins") {
  SECTION("O wins diagonally") {
    REQUIRE(Board("xxo-oxo--").EvaluateBoard() == BoardState::Owins);
  }

  SECTION("O wins horizontally") {
    REQUIRE(Board("oooxx-x--").EvaluateBoard() == BoardState::Owins);
  }

  SECTION("O wins vertically") {
    REQUIRE(Board("-xox-ox-o").EvaluateBoard() == BoardState::Owins);
  }
}

TEST_CASE("Unreachable board states") {
  SECTION("Too many Os, no one would win") {
    REQUIRE(Board("XOO------").EvaluateBoard() == BoardState::UnreachableState);
  }

  SECTION("Too many Xs, no one would win") {
    REQUIRE(Board("XXO-----X").EvaluateBoard() == BoardState::UnreachableState);
  }

  SECTION("Too many Xs, even though X would win") {
    REQUIRE(Board("XOOX--X-X").EvaluateBoard() == BoardState::UnreachableState);
  }

  SECTION("Too many Os, even though O would win") {
    REQUIRE(Board("--XOOO--X").EvaluateBoard() == BoardState::UnreachableState);
  }

  SECTION("Both X and O win, unreachable") {
    REQUIRE(Board("xxx---ooo").EvaluateBoard() == BoardState::UnreachableState);
  }

  SECTION("X would win, but same number of Xs and Os") {
    REQUIRE(Board("XXXO---OO").EvaluateBoard() == BoardState::UnreachableState);
  }
}

TEST_CASE("Larger (4x4) board") {
  SECTION("O wins horizontally") {
    REQUIRE(Board("ooooxxx--------X").EvaluateBoard() == BoardState::Owins);
  }

  SECTION("X wins diagonally") {
    REQUIRE(Board("Xooo-X----X----X").EvaluateBoard() == BoardState::Xwins);
  }

  SECTION("Unreachable state (Too many Os)") {
    REQUIRE(Board("XOOX----OXXOOOOX").EvaluateBoard() == BoardState::UnreachableState);
  }

  SECTION("Unreachable state (Too many Xs)") {
    REQUIRE(Board("XOOX----OXXOXXXO").EvaluateBoard() == BoardState::UnreachableState);
  }

  SECTION("Unreachable state (Both X and O win)") {
    REQUIRE(Board("xxxx----oooo----").EvaluateBoard() == BoardState::UnreachableState);
  }

  SECTION("Unreachable state (X would win, but same number of Xs and Os)") {
    REQUIRE(Board("XXXX-O--OOO-----").EvaluateBoard() == BoardState::UnreachableState);
  }
}