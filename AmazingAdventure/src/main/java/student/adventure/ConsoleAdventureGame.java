package student.adventure;

import maplayout.MapLayout;

import java.util.Scanner;

public class ConsoleAdventureGame extends AdventureGame {

  // Scanner object for player interactivity and getting user commands from console
  private final Scanner scanner;

  /**
   * student.adventure.AdventureGame constructor that initializes the first environment variables and the initial
   * player stats variables
   *
   * @param mapLayout represents the data that the game map is based on
   */
  public ConsoleAdventureGame(MapLayout mapLayout) {
    super(mapLayout);
    this.scanner = new Scanner(System.in);
  }

  /**
   * The actual sequence of events in the game from start to finish as it is being played
   */
  public void playGame() {
    displayStartScreen();
    promptGameStart();
    do {
      displaySituation();
      getConsoleInput();
      parsePlayerInput();
      applyAreaAffects();
      reactToPlayerInput();
      updateArea();
    } while (!isGameOver());

    displayGameOverMessage();
  }

  /**
   * Constructs and prints output for the current area and player status
   */
  private void displaySituation() {
    gameStatusMessage += System.lineSeparator();
    gameStatusMessage += getSituationMessage();
    gameStatusMessage += "> ";
    displayOutput();
  }

  /**
   * Constructs and prints output for the end of game message
   */
  private void displayGameOverMessage() {
    gameStatusMessage += getGameOverMessage();
    displayOutput();
  }

  /**
   * Constructs and prints output for start message such as available commands and setting
   */
  private void displayStartScreen() {
    gameStatusMessage += getStartScreenMessage();
    displayOutput();
  }

  /**
   * Prompts for enter input and waits for the player to hit enter before starting the game
   */
  private void promptGameStart() {
    String output = "";
    output = output.concat("Press enter to start game!");
    output = output.concat(System.lineSeparator());
    output = output.concat("> ");
    gameStatusMessage += output;
    displayOutput();
    getConsoleInput();
  }

  /**
   * Displays the accumulated output string to the console and flushes the output string
   */
  private void displayOutput() {
    System.out.print(gameStatusMessage);
    gameStatusMessage = "";
  }

  /**
   * Gets from the terminal input the player's input for game commands
   */
  private void getConsoleInput() {
    currentUserInput = scanner.nextLine();
  }
}
