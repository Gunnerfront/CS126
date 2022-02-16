package mineopoly_three.strategy;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;

import java.awt.Point;
import java.util.Random;

public class AssignmentStrategy implements MinePlayerStrategy {
  final static String PLAYER_NAME = "Gunnerside";

  // Round Info
  private int maxRobotCharge;
  private int maxInventorySize;
  private int winningScore;
  private boolean isRedPlayer;

  // Current Robot Info
  private int robotCharge;
  private int robotInventorySize;
  private ItemType preferredItem;           // This is the item (gem) that the robot will look for first
  private Point robotLocation;

  // Environment Info
  private int boardSize;
  private PlayerBoardView currentBoard;

  /**
   * Reset robot and initialize each rounds new values
   *
   * @param boardSize The length and width of the square game board
   * @param maxInventorySize The maximum number of items that your player can carry at one time
   * @param maxCharge The amount of charge your robot starts with (number of tile moves before needing to recharge)
   * @param winningScore The first player to reach this score wins the round
   * @param startingBoard A view of the GameBoard at the start of the game. You can use this to pre-compute fixed
   *                       information, like the locations of market or recharge tiles
   * @param startTileLocation A Point representing your starting location in (x, y) coordinates
   *                              (0, 0) is the bottom left and (boardSize - 1, boardSize - 1) is the top right
   * @param isRedPlayer True if this strategy is the red player, false otherwise
   * @param random A random number generator, if your strategy needs random numbers you should use this.
   */
  @java.lang.Override
  public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore,
                         PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer, Random random) {
    resetRobot();
    this.boardSize = boardSize;
    this.maxInventorySize = maxInventorySize;
    this.maxRobotCharge = maxCharge;
    this.robotCharge = maxCharge;
    this.robotLocation = startTileLocation;
    this.winningScore = winningScore;
    this.currentBoard = startingBoard;
    this.isRedPlayer = isRedPlayer;
  }

  /**
   * Decides and returns the robot's action this turn based on the robot's current status as well as the current
   * environment.
   *
   * @param boardView A PlayerBoardView object representing all the information about the board and the other player
   *                   that your strategy is allowed to access
   * @param economy The GameEngine's economy object which holds current prices for resources
   * @param currentCharge The amount of charge your robot has (number of tile moves before needing to recharge)
   * @param isRedTurn For use when two players attempt to move to the same spot on the same turn
   *                   If true: The red player will move to the spot, and the blue player will do nothing
   *                   If false: The blue player will move to the spot, and the red player will do nothing
   * @return
   */
  @java.lang.Override
  public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {
    updateRobot(boardView, currentCharge);
    RobotPriority priority = determineRobotPriority();

    // Choose Action
    TurnAction action;
    switch (priority) {
      case CHARGE:
        action = goCharge();
        break;
      case SELL:
        action = goSell();
        break;
      case PICKUP_HERE:
        action = pickupHere();
        break;
      case MINE_PREFERRED_NEARBY:
        action = minePreferredNearby();
        break;
      case MINE_OTHER_NEARBY:
        action = mineOtherNearby();
        break;
      case MINE_OTHER:
        action = mineOther();
        break;
      case PICKUP_ELSEWHERE:
        action = pickupElsewhere();
        break;
      case NULL:
      default:
        action = null;
    }

    return action;
  }

  /**
   * Runs when the player successfully executed a PICK_UP TurnAction on their last turn.
   * With this strategy it increases the inventory of the robot by one regardless of the item.
   *
   * @param itemReceived The item received from the player's TurnAction on their last turn
   */
  @java.lang.Override
  public void onReceiveItem(InventoryItem itemReceived) {
    robotInventorySize++;
  }

  /**
   * Runs when the player steps on a market tile of their color with at least one gem in their inventory.
   * With this strategy it resets the robot's inventory to zero and rotates the robots preferred item.
   *
   * @param totalSellPrice The combined sell price for all items in your strategy's inventory
   */
  @java.lang.Override
  public void onSoldInventory(int totalSellPrice) {
    robotInventorySize = 0;
    rotatePreferredItem();
  }

  /**
   * Returns the name of the player in the round
   *
   * @return a String representing the robot player's name
   */
  @java.lang.Override
  public String getName() {
    return PLAYER_NAME;
  }

  /**
   * Resets the robot's non-initializable values at the end of a round, when one player wins
   *
   * @param pointsScored The total number of points this strategy scored
   * @param opponentPointsScored The total number of points the opponent's strategy scored
   */
  @java.lang.Override
  public void endRound(int pointsScored, int opponentPointsScored) {
    resetRobot();
  }

  /**
   * Where the robot data for values not initialized with new round values will be reset to default values
   */
  private void resetRobot() {
    robotInventorySize = 0;
    preferredItem = ItemType.DIAMOND;
  }
}
