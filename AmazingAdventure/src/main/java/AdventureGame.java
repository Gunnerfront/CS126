import maplayout.*;
import java.util.Scanner;

public class AdventureGame {
  // The various commands a player has access to via the input channel
  static final String QUIT_COMMAND = "quit";
  static final String EXIT_COMMAND = "exit";
  static final String GO_COMMAND = "go";
  static final String USE_COMMAND = "use";
  static final String TAKE_COMMAND = "take";
  static final String DROP_COMMAND = "drop";

  // Item flag constants
  static final int NO_ITEM = 0;
  static final int BASEBALL_BAT = 1;
  static final int MEDKIT = 2;
  static final int BUS_KEY = 3;

  // Amount that the baseball bat item reduces the current threat level
  static final int BASEBALL_BAT_EFFECTIVENESS = 5;

  // The injury level at which the player dies and the game ends
  static final int MAX_INJURY_LEVEL = 3;

  // The area ids controlling where the bus shortcut is in the game
  static final int BUS_ENTRANCE_ID = 8;
  static final int BUS_EXIT_ID = 15;

  // Player Stats Variables
  int currentInjuryLevel;
  int inventoryItem;

  // Game Environment Variables
  MapLayout mapLayout;
  int currentAreaId;
  int currentThreatLevel;

  // Scanner object for player interactivity and getting user commands
  Scanner scanner;

  public AdventureGame(MapLayout mapLayout) {
    this.mapLayout = mapLayout;
    this.currentAreaId = mapLayout.getStartAreaId();
    this.inventoryItem = NO_ITEM;
    this.currentThreatLevel = getCurrentAreaInitialThreatLevel();
    this.scanner = new Scanner(System.in);
  }

  public void playGame() {
    displayStartMessage();
    do {
      displaySituation();
      getPlayerInput();
      endTurn();
    } while (!isGameOver());

    displayGameOverMessage();
  }

  private void displayStartMessage() {
    System.out.println(mapLayout.getStartMessage());
  }

  private void displayGameOverMessage() {
    if (isAtEnd()) {
      System.out.println(getCurrentAreaDescription());
    } else if (isPlayerDead()) {
      System.out.println(mapLayout.getDeathMessage());
    }
  }

  private void displaySituation() {
    System.out.println(getCurrentAreaDescription());
    System.out.println("Current Injury Sustained: " + currentInjuryLevel + " (death at " + MAX_INJURY_LEVEL + ")");
    System.out.println("Threat Level: " + currentThreatLevel);
    System.out.println("Inventory: " + mapLayout.findItemDescription(inventoryItem));
    System.out.print("> ");
  }

  private void getPlayerInput() {
    String userInput = scanner.nextLine();
    String command = parseCommand(userInput);

    switch (command) {
      case QUIT_COMMAND:
      case EXIT_COMMAND:
        System.out.println("Exiting game...");
        System.exit(0);
        break;

      case GO_COMMAND:
        // Attempt to change the player's location
        String directionName = parseArgument();
        int direction = verifyGoDirection(directionName);
        movePlayer(direction);
        break;

      case TAKE_COMMAND:
      case DROP_COMMAND:
        // The drop and take commands replaces your inventory item with the item in the current MapArea
        inventoryItem = mapLayout.findMapArea(currentAreaId).getItemInArea();
        break;

      case USE_COMMAND:
        // May change the player's location
        processItemAction();
        break;

      default:
        System.out.println("You ponder what it means to " + command);
    }
  }

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

  private void useBusKey() {
    if (BUS_ENTRANCE_ID == currentAreaId) {
      currentAreaId = BUS_EXIT_ID;
    } else {
      System.out.println("I cannot use this item here.");
    }
  }

  private void useMedKit() {
    currentInjuryLevel = 0;
  }

  private void useBaseballBat() {
    currentThreatLevel -= BASEBALL_BAT_EFFECTIVENESS;
    if (currentThreatLevel < 0) {
      currentThreatLevel = 0;
    }
  }

  private void useNoItem() {
    System.out.println("You scramble to find a useful item in your backpack but find nothing.");
  }

  private String getCurrentAreaDescription() {
    return mapLayout.findMapArea(currentAreaId).getDescription();
  }

  private int getCurrentAreaInitialThreatLevel() {
    return mapLayout.findMapArea(currentAreaId).getInitialThreatLevel();
  }

  private boolean isGameOver() {
    return isAtEnd() || isPlayerDead();
  }

  private boolean isAtEnd() {
    return currentAreaId == mapLayout.getEndAreaId();
  }

  private boolean isPlayerDead() {
    return currentInjuryLevel == MAX_INJURY_LEVEL;
  }
}
