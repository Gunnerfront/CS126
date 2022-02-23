package student.adventure;

import maplayout.MapLayout;

import java.util.*;

public class WebAdventureGame extends AdventureGame {
  // Lists of argument options for the various commands, used for Web API
  private static final List<String> EMPTY_ARGUMENT_LIST = new ArrayList<>(Arrays.asList(""));
  private static final List<String> GO_ARGUMENT_LIST = new ArrayList<>(Arrays.asList(
      "NORTH", "EAST", "SOUTH", "WEST"
  ));

  // Web API variables
  private Map<String, List<String>> commandOptions;
  private boolean hasExited;

  /**
   * AdventureGame constructor that initializes the first environment variables and the initial
   * player stats variables
   *
   * @param mapLayout represents the data that the game map is based on
   */
  public WebAdventureGame(MapLayout mapLayout) {
    super(mapLayout);
    commandOptions = new HashMap<>();
    commandOptions.putIfAbsent(START_COMMAND, EMPTY_ARGUMENT_LIST);
    loadStartScreenMessage();
    hasExited = false;
  }

  /**
   * Acts on the WebAdventureGame by one turn, based on the specified command.
   *
   * @param inputCommand the command that you want the game to react to
   */
  public void performTurn(String inputCommand) {
    flushPreviousTurnMessage();
    loadWebInput(inputCommand);
    if (isGameOver()) {
      loadGameOverMessage();
      setGameOverCommands();
    } else {
      reactToPlayerInput();
      applyAreaAffects();
      updateArea();
      loadSituationMessage();
    }
  }

  /**
   * Gets the mapping of commands to arguments needed for the GameStatus class used in AdventureGameService.
   *
   * @return the Map of commands to their respective accepted arguments
   */
  public Map<String, List<String>> getCommandOptions() {
    return commandOptions;
  }

  /**
   * Gets the String representation of the game's output each turn.
   *
   * @return the String of the game's output
   */
  public String getGameStatusMessage() {
    return gameStatusMessage;
  }

  @Override
  /**
   * Handles the web-based game so that the exit screen is reached uppon hitting exit.
   */
  protected void processExitGame() {
    super.processExitGame();
    loadGameOverMessage();
    hasExited = true;
  }

  /**
   * Edits the command list to only include the quit command for the game over scenario.
   */
  private void setGameOverCommands() {
    commandOptions = new HashMap<>();
    commandOptions.putIfAbsent(QUIT_COMMAND, EMPTY_ARGUMENT_LIST);
  }

  /**
   * Accepts an unparsed command String, parsing it and then loading in the usual game commands if the
   * command happens to be the START_COMMAND.
   *
   * @param input String representing the command and arguments
   */
  private void loadWebInput(String input) {
    currentUserInput = input;
    parsePlayerInput();
    reloadCommands();
  }

  /**
   * Checks for the START_COMMAND, then sets the usual game commands if it is START_COMMAND. Else, does nothing.
   * Used to allow player to access the normal commands once they hit start button.
   */
  private void reloadCommands() {
    if (currentUserInputTokens[0].equalsIgnoreCase(START_COMMAND)) {
      setGameCommands();
    }
  }

  /**
   * Adds to the status message the first text which the user sees before starting the game. This includes
   * the setting description and the command list. For initializing the game before Start button is pressed.
   */
  private void loadStartScreenMessage() {
    gameStatusMessage += getStartScreenMessage();
    gameStatusMessage = gameStatusMessage.concat("Press button to start game!");
    gameStatusMessage = gameStatusMessage.concat(System.lineSeparator());
  }

  /**
   * Loads into the status message the text that is output when the game is over, after the game has been played
   * through.
   */
  private void loadGameOverMessage() {
    gameStatusMessage += getGameOverMessage();
  }

  /**
   * Loads into the status message the new area and player status information after the player makes an action.
   */
  private void loadSituationMessage() {
    if (!hasExited) {
      gameStatusMessage += getSituationMessage();
    }
  }

  /**
   * Redefines the game command option list to use the usual commands available DURING the game.
   */
  private void setGameCommands() {
    commandOptions = new Hashtable<>();
    commandOptions.putIfAbsent(USE_COMMAND, EMPTY_ARGUMENT_LIST);
    commandOptions.putIfAbsent(TAKE_COMMAND, EMPTY_ARGUMENT_LIST);
    commandOptions.putIfAbsent(PATH_COMMAND, EMPTY_ARGUMENT_LIST);
    commandOptions.putIfAbsent(GO_COMMAND, GO_ARGUMENT_LIST);
    commandOptions.putIfAbsent(QUIT_COMMAND, EMPTY_ARGUMENT_LIST);
  }

  /**
   * Resets the status message to an empty string so that previous status information does not reappear on the
   * player's next screen.
   */
  private void flushPreviousTurnMessage() {
    gameStatusMessage = "";
  }

  // METHODS USED FOR TESTING BELOW

  public int getCurrentInjuryLevel() {
    return currentInjuryLevel;
  }
}
