package student.adventure;

import maplayout.MapLayout;

import java.util.*;

public class WebAdventureGame extends AdventureGame {
  // Lists of argument options for the various commands, used for Web API
  private static final List<String> EMPTY_ARGUMENT_LIST = new ArrayList<>(Arrays.asList(""));
  private static final List<String> GO_ARGUMENT_LIST = new ArrayList<>(Arrays.asList(
      "NORTH", "EAST", "SOUTH", "WEST"
  ));
  private static final String START_COMMAND = "START";

  // Web API variables
  private Map<String, List<String>> commandOptions;

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
  }

  public void performTurn(String inputCommand) {
    flushPreviousTurnMessage();
    loadWebInput(inputCommand);
    if (isGameOver()) {
      loadGameOverMessage();
    } else {
      applyAreaAffects();
      reactToPlayerInput();
      updateArea();
      loadSituationMessage();
    }
  }

  public Map<String, List<String>> getCommandOptions() {
    return commandOptions;
  }

  public String getGameStatusMessage() {
    return gameStatusMessage;
  }

  private void loadWebInput(String input) {
    currentUserInput = input;
    parsePlayerInput();
    reloadCommands();
  }

  private void reloadCommands() {
    if (currentUserInputTokens[0].equalsIgnoreCase(START_COMMAND)) {
      repopulateCommandOptions();
    }
  }

  private void loadStartScreenMessage() {
    gameStatusMessage += getStartScreenMessage();
    gameStatusMessage = gameStatusMessage.concat("Press button to start game!");
    gameStatusMessage = gameStatusMessage.concat(System.lineSeparator());
  }

  private void loadGameOverMessage() {
    gameStatusMessage += getGameOverMessage();
  }

  private void loadSituationMessage() {
    gameStatusMessage += getSituationMessage();
  }

  private void repopulateCommandOptions() {
    commandOptions = new Hashtable<>();
    commandOptions.putIfAbsent(USE_COMMAND, EMPTY_ARGUMENT_LIST);
    commandOptions.putIfAbsent(TAKE_COMMAND, EMPTY_ARGUMENT_LIST);
    commandOptions.putIfAbsent(PATH_COMMAND, EMPTY_ARGUMENT_LIST);
    commandOptions.putIfAbsent(GO_COMMAND, GO_ARGUMENT_LIST);
    commandOptions.putIfAbsent(QUIT_COMMAND, EMPTY_ARGUMENT_LIST);
  }

  private void flushPreviousTurnMessage() {
    gameStatusMessage = "";
  }
}
