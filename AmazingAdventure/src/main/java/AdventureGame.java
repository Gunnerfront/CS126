import maplayout.*;
import java.util.Scanner;

public class AdventureGame {
  // The various commands a player has access to via the input channel
  static final String[] COMMANDS = {"quit", "exit", "go", "use", "take", "drop"};

  // Item flag constants
  static final int NO_ITEM = 0;
  static final int BASEBALL_BAT = 1;
  static final int MEDKIT = 2;
  static final int BUS_KEY = 3;

  // The injury level at which the player dies and the game ends
  static final int MAX_INJURY_LEVEL = 3;

  // Player Stats Variables
  int currentInjuryLevel;
  int inventoryItem;

  // Game Environment Variables
  MapLayout mapLayout;
  int currentAreaId;
  int currentThreatLevel;

  public AdventureGame(MapLayout mapLayout) {
    this.mapLayout = mapLayout;
    this.currentAreaId = mapLayout.getStartAreaId();
    this.inventoryItem = NO_ITEM;
    this.currentThreatLevel = getCurrentAreaInitialThreatLevel();
  }

  public void playGame() {
    displayStartMessage();
    do {
      displaySituation();
      String userInput = getUserInput();

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
