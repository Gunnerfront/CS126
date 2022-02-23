package student.adventure;

import maplayout.MapLayout;

import java.util.*;

public class WebAdventureGame extends AdventureGame {
  // Lists of argument options for the various commands, used for Web API
  private static final List<String> EMPTY_ARGUMENT_LIST = new ArrayList<>();
  private static final List<String> GO_ARGUMENT_LIST = new ArrayList<>(Arrays.asList(
      "NORTH", "EAST", "SOUTH", "WEST"
  ));

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
    commandOptions.putIfAbsent("Start", EMPTY_ARGUMENT_LIST);
    loadStartScreenMessage();
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
    gameStatusMessage = gameStatusMessage.concat("> ");
  }

  private void initializeCommandOptions() {
    commandOptions = new Hashtable<>();
    commandOptions.putIfAbsent(USE_COMMAND, EMPTY_ARGUMENT_LIST);
    commandOptions.putIfAbsent(TAKE_COMMAND, EMPTY_ARGUMENT_LIST);
    commandOptions.putIfAbsent(PATH_COMMAND, EMPTY_ARGUMENT_LIST);
    commandOptions.putIfAbsent(GO_COMMAND, GO_ARGUMENT_LIST);
    commandOptions.putIfAbsent(QUIT_COMMAND, EMPTY_ARGUMENT_LIST);
  }

  private void getWebInput() {

  }

  private void endTurn() {
    gameStatusMessage = "";
  }

  public Map<String, List<String>> getCommandOptions() {
    return commandOptions;
  }

  public String getGameStatusMessage() {
    return gameStatusMessage;
  }
}
