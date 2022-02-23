import maplayout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class AdventureGame {
  // The various commands a player has access to via the input channel
  private static final String QUIT_COMMAND = "quit";
  private static final String EXIT_COMMAND = "exit";
  private static final String GO_COMMAND = "go";
  private static final String USE_COMMAND = "use";
  private static final String TAKE_COMMAND = "take";
  private static final String DROP_COMMAND = "drop";
  private static final String PATH_COMMAND = "path";

  // Item flag constants
  private static final int NO_ITEM = 0;
  private static final int BASEBALL_BAT = 1;
  private static final int MEDKIT = 2;
  private static final int BUS_KEY = 3;

  // Amount that the baseball bat item reduces the current threat level
  private static final int BASEBALL_BAT_EFFECTIVENESS = 5;

  // The injury level at which the player dies and the game ends
  private static final int MAX_INJURY_LEVEL = 3;

  // The area ids controlling where the bus shortcut is in the game
  private static final int BUS_ENTRANCE_ID = 8;
  private static final int BUS_EXIT_ID = 15;

  // The input that the player has entered during this turn of the game; first String is
  // the command, second String is the argument
  private String[] currentUserInput;

  // Player Stats Variables
  private int currentInjuryLevel;
  private int inventoryItem;
  private boolean hasMoved;

  // Game Environment Variables
  private final MapLayout mapLayout;
  private int currentAreaId;
  private int currentThreatLevel;
  private int currentItemOnGround;
  private final List<Integer> areaTraversalHistory;

  // Scanner object for player interactivity and getting user commands if in console
  private final Scanner scanner;

  // If the game is launched in web mode rather than in console
  private final boolean isOnWeb;

  /**
   * AdventureGame constructor that initializes the first environment variables and the initial player stats variables
   *
   * @param mapLayout represents the data that the game map is based on
   */
  public AdventureGame(MapLayout mapLayout) {
    this.mapLayout = mapLayout;
    this.currentAreaId = mapLayout.getStartAreaId();
    this.inventoryItem = NO_ITEM;
    this.currentThreatLevel = getCurrentAreaInitialThreatLevel();
    this.scanner = new Scanner(System.in);
    this.currentUserInput = new String[2];
    this.currentUserInput[0] = "";
    this.currentUserInput[1] = "";
    this.currentInjuryLevel = 0;
    this.currentItemOnGround = mapLayout.findMapArea(currentAreaId).getItemInArea();
    this.areaTraversalHistory = new ArrayList<>();
    this.areaTraversalHistory.add(Integer.valueOf(mapLayout.findMapArea(currentAreaId).getAreaId()));

    this.isOnWeb = false;     // TODO: Change this to get result of function
  }

  /**
   * The actual sequence of events in the game from start to finish as it is being played
   */
  public void playGame() {
    displayStartScreen();
    promptGameStart();
    do {
      displaySituation();
      String playerInput = getInput();
      parsePlayerInput(playerInput);
      applyAreaAffects();
      reactToPlayerInput();
      updateArea();
    } while (!isGameOver());

    displayGameOverMessage();
  }

  /**
   * Prints into the terminal the map's start message as well as the player commands
   */
  private void displayStartScreen() {
    String output = "";
    output = output.concat(mapLayout.getStartMessage());
    output = output.concat(System.lineSeparator());
    output = output.concat("PLAYER COMMANDS:");
    output = output.concat(System.lineSeparator());
    output = output.concat(EXIT_COMMAND + ", " + QUIT_COMMAND + ", " + TAKE_COMMAND + " <item>, " + DROP_COMMAND + " "
        + "<item>, " + GO_COMMAND + " <direction>, " + USE_COMMAND + " <item>, " + PATH_COMMAND);
    output = output.concat(System.lineSeparator());
    displayOutput(output);
  }

  /**
   * Waits for the player to hit enter before starting the game
   */
  private void promptGameStart() {
    String output = "";
    output = output.concat("Press enter to start game!");
    output = output.concat(System.lineSeparator());
    output = output.concat("> ");
    displayOutput(output);
    getInput();
  }

  /**
   * Prints to terminal either the death message or the win message depending on
   * if the player is at the end or if they are at the max injury level
   */
  private void displayGameOverMessage() {
    String output = "";
    if (isPlayerDead()) {
      output = output.concat(mapLayout.getDeathMessage());
    } else if (isAtEnd()) {
      output = output.concat(getCurrentAreaDescription());
    }
    output = output.concat(System.lineSeparator());
    displayOutput(output);
  }

  /**
   * Prints to terminal the information each turn, including the area description, player health info,
   * area threat info, and item info
   */
  private void displaySituation() {
    String output = "";
    output = output.concat(getCurrentAreaDescription());
    output = output.concat(System.lineSeparator());
    output = output.concat("Current Injury Sustained: " + currentInjuryLevel + " (death at " + MAX_INJURY_LEVEL + ")");
    output = output.concat(System.lineSeparator());
    output = output.concat("Threat Level: " + currentThreatLevel);
    output = output.concat(System.lineSeparator());
    output = output.concat("Item on ground: " + mapLayout.findItemDescription(currentItemOnGround));
    output = output.concat(System.lineSeparator());
    output = output.concat("Inventory: " + mapLayout.findItemDescription(inventoryItem));
    output = output.concat(System.lineSeparator());
    output = output.concat("> ");
    displayOutput(output);
  }

  /**
   * Gets from the terminal input the player's input for game commands
   *
   * @return the unedited String containing the player's input
   */
  private String getConsoleInput() {
    return scanner.nextLine();
  }

  /**
   * Separates the users input into the currentUserInputArray. The first word entered is the command
   * and goes into the ZERO index, and the optional second word entered is the argument that
   * goes into the ONE index.
   *
   * @param userInputString the unparsed player input
   */
  private void parsePlayerInput(String userInputString) {
    userInputString =  userInputString.trim();
    String command = "";
    String argument = "";
    boolean encounteredSpace = false;
    for (int letterNumber = 0; letterNumber < userInputString.length(); letterNumber++) {
      if (userInputString.charAt(letterNumber) < 33 && command.length() == 0) {
        // If current char is a space and command hasn't been parsed yet
        continue;
      } else if (userInputString.charAt(letterNumber) < 33) {
        // When command has been parsed and space character has been met
        encounteredSpace = true;
      } else if (userInputString.charAt(letterNumber) >= 33 && !encounteredSpace) {
        // When current character is not a space and word separating space HAS NOT been met
        command += userInputString.charAt(letterNumber);
      } else if (userInputString.charAt(letterNumber) >= 33 && encounteredSpace) {
        // When current character is not a space and word separating space HAS been met
        argument += userInputString.charAt(letterNumber);
      }
    }

    currentUserInput[0] = command;
    currentUserInput[1] = argument;
  }

  /**
   * Applies the environment effects against the player's stats. For now this is just
   * the threat level of the area affecting the player's injury level.
   */
  private void applyAreaAffects() {
    int squirrelAttackChance = ThreadLocalRandom.current().nextInt(0, 11);
    if (squirrelAttackChance < currentThreatLevel) {
      displayOutput("You get mauled by a squirrel!\n");
      currentInjuryLevel++;
    } else {
      displayOutput("You managed to avoid the squirrels, for now.\n");
    }
  }

  /**
   * Updates the game depending on the previously parsed player input and the corresponding commands.
   */
  private void reactToPlayerInput( ) {
    String command = currentUserInput[0];

    switch (command) {
      case QUIT_COMMAND:
      case EXIT_COMMAND:
        displayOutput("Exiting game...\n");
        System.exit(0);
        break;

      case GO_COMMAND:
        // Attempt to change the player's location
        processGoCommand();
        break;

      case TAKE_COMMAND:
      case DROP_COMMAND:
        // The drop and take commands replaces your inventory item with the item in the current MapArea
        processTakeCommand();
        break;

      case USE_COMMAND:
        // May change the player's location
        processItemAction();
        hasMoved = false;
        break;

      case PATH_COMMAND:
        displayPlayerPath();
        break;
      default:
        displayOutput("You ponder what it means to '" + command + "'.\n");
    }
  }

  /**
   * Updates the environment variables to the new area if the player has moved. If not moved, the
   * environment variable for threat level is incremented.
   */
  private void updateArea() {
    if (hasMoved) {
      MapArea newArea = mapLayout.findMapArea(currentAreaId);
      currentThreatLevel = newArea.getInitialThreatLevel();
      currentItemOnGround = newArea.getItemInArea();
    } else {
      currentThreatLevel++;
    }
  }

  /**
   * Checks the player-provided argument for move direction and if valid, moves the player. This function
   * only runs if the player inputs the go command
   */
  private void processGoCommand() {
    String directionName = currentUserInput[1];
    int direction = findValidDirectionId(directionName);
    if (direction == -1) {
      displayOutput("You did not go in a valid direction and ran into a squirrel roadblock!\n");
      hasMoved = false;
    } else {
      movePlayer(direction);
      hasMoved = true;
    }
  }

  /**
   * Checks the area for any items to pick up and prints the corresponding dialogue. This function
   * should only run if the player inputs a drop or take command.
   */
  private void processTakeCommand() {
    inventoryItem = mapLayout.findMapArea(currentAreaId).getItemInArea();
    if (inventoryItem != NO_ITEM) {
      displayOutput("You pick up a " + mapLayout.findItemDescription(inventoryItem) + "\n");
    } else {
      displayOutput("There is nothing new to pick up here!\n");
    }
    hasMoved = false;
  }

  /**
   * Moves the player to a new area and displays the dialogue verifying the movement
   * to the player.
   *
   * @param areaId representing the id of the new area the player moves to
   */
  private void movePlayer(int areaId) {
    currentAreaId = areaId;
    areaTraversalHistory.add(Integer.valueOf(areaId));
    displayOutput("You successfully moved to a new area!\n");
  }

  /**
   * Checks the map layout for the id of the area pointed to by the specified direction
   *
   * @param directionName the direction in which you want to move represented as a String
   * @return integer id of the area you want to move to, -1 if direction was invalid
   */
  private int findValidDirectionId(String directionName) {
    return mapLayout.findMapArea(currentAreaId).findDirection(directionName);
  }

  /**
   * Checks the current inventory item and performs the corresponding item's action, if any.
   */
  private void processItemAction() {
    switch (inventoryItem) {
      case BUS_KEY:
        useBusKey();
        break;
      case MEDKIT:
        useMedKit();
        break;
      case BASEBALL_BAT:
        useBaseballBat();
        break;
      case NO_ITEM:
      default:
        useNoItem();
    }
    inventoryItem = NO_ITEM;
  }

  /**
   * If the player is in a bus entry area with the bus key, the player is transported to the
   * new area represented by the id stored in BUS_EXIT_ID
   */
  private void useBusKey() {
    if (BUS_ENTRANCE_ID == currentAreaId) {
      currentAreaId = BUS_EXIT_ID;
      displayOutput("You use the bus key and drive on.\n");
    } else {
      displayOutput("I cannot use this item here.\n");
    }
  }

  /**
   * Heals the player by reducing their injury level.
   */
  private void useMedKit() {
    currentInjuryLevel = 0;
    displayOutput("You use the MedKit on your wounds.\n");
  }

  /**
   * Allows the player to reduce the current area's squirrel threat level.
   */
  private void useBaseballBat() {
    currentThreatLevel -= BASEBALL_BAT_EFFECTIVENESS;
    if (currentThreatLevel < 0) {
      currentThreatLevel = 0;
    }
    displayOutput("You use the baseball bat and pummel some squirrels.\n");
  }

  /**
   * Displays the dialogue for if the player attempts to use an item when no
   * item is in their inventory.
   */
  private void useNoItem() {
    displayOutput("You scramble to find a useful item in your backpack but find nothing.\n");
  }

  /**
   * Gets the description of the current area as described in the map layout.
   *
   * @return a String description of the current area
   */
  private String getCurrentAreaDescription() {
    return mapLayout.findMapArea(currentAreaId).getDescription();
  }

  /**
   * Gets the area's initial threat level as described in the map layout.
   *
   * @return integer representing the area's initial threat level
   */
  private int getCurrentAreaInitialThreatLevel() {
    return mapLayout.findMapArea(currentAreaId).getInitialThreatLevel();
  }

  /**
   * Checks if the game is over based on if the player is dead or if they've reached the end.
   *
   * @return true if the game is over, false if not
   */
  private boolean isGameOver() {
    return isAtEnd() || isPlayerDead();
  }

  /**
   * Checks if the player is at the end area as described by the map layout.
   *
   * @return true if the player is at the end area, false if not
   */
  private boolean isAtEnd() {
    return currentAreaId == mapLayout.getEndAreaId();
  }

  /**
   * Checks if the player is at the max injury level, which signifies player death.
   *
   * @return true if the injury level is at max level, false if not
   */
  private boolean isPlayerDead() {
    return currentInjuryLevel >= MAX_INJURY_LEVEL;
  }

  // NEW METHODS

  /**
   * Gets a String of names of the areas the player has visited in order from first visited to last visited.
   *
   * @return String representing the player's map traversal
   */
  private String getAreaTraversalHistoryString() {
    String output = "";
    for (int areaNumber = 0; areaNumber < areaTraversalHistory.size(); areaNumber++) {
      int areaId = areaTraversalHistory.get(areaNumber);
      String line = (areaNumber + 1) + ": " + mapLayout.findMapArea(areaId).getAreaName() + "\n";
      output = output.concat(line);
    }
    return output;
  }

  /**
   * Displays the output to the user, either to the console, or through the API depending on
   * if the game was launched via the API.
   *
   * @param output the String that the player is to see
   */
  private void displayOutput(String output) {
    if (isOnWeb) {
      System.out.println("Implement api output!!");
    } else {
      System.out.print(output);
    }
  }

  /**
   * Gets the input from the player either from the console, or from the API depending on if the game was
   * launched via the API.
   *
   * @return
   */
  private String getInput() {
    if (isOnWeb) {
      return "Implement api input!!";
    } else {
      return getConsoleInput();
    }
  }

  /**
   * Outputs to the player the player's past locations
   */
  private void displayPlayerPath() {
    String output = "You take a moment to remember how you got here:";
    output = output.concat(System.lineSeparator());
    output = output.concat(getAreaTraversalHistoryString());
    output = output.concat(System.lineSeparator());
    displayOutput(output);
  }
}
