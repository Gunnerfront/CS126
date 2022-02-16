package mineopoly_three.strategy;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.tiles.TileType;

import java.awt.Point;
import java.util.*;

public class AssignmentStrategy implements MinePlayerStrategy {
  private static final String PLAYER_NAME = "Gunnerside";

  // The battery percentage at which the robot decides to charge
  private static final double LOW_BATTERY_LEVEL = 0.20;

  // Round Info
  private int maxRobotCharge;
  private int maxInventorySize;
  private boolean isRedPlayer;
  private boolean hasHadAutominer;

  // Current Robot Info
  private int robotCharge;
  private int robotInventorySize;
  private ItemType preferredItem;           // This is the item (gem) that the robot will look for first
  private int autominerCount;

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
    this.currentBoard = startingBoard;
    this.isRedPlayer = isRedPlayer;
  }

  // ROBOT ACTION METHODS

  /**
   * Decides and returns the robot's action this turn based on the robot's priority.
   *
   * @param boardView A PlayerBoardView object representing all the information about the board and the other player
   *                   that your strategy is allowed to access
   * @param economy The GameEngine's economy object which holds current prices for resources
   * @param currentCharge The amount of charge your robot has (number of tile moves before needing to recharge)
   * @param isRedTurn For use when two players attempt to move to the same spot on the same turn
   *                   If true: The red player will move to the spot, and the blue player will do nothing
   *                   If false: The blue player will move to the spot, and the red player will do nothing
   * @return TurnAction the robot action that the strategy determines is best in this turn
   */
  @java.lang.Override
  public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {
    updateRobot(boardView, currentCharge);
    RobotPriority priority = determineRobotPriority();

    // Choose Action
    TurnAction action = switch ( priority ) {
      case CHARGE -> goCharge();
      case USE_AUTOMINER -> useAutominer();
      case SELL -> goSell();
      case PICKUP_HERE -> pickupHere();
      case MINE_HERE -> mineHere();
      case MINE_ELSEWHERE -> mineNearby();
      default -> null;
    };

    return action;
  }

  private TurnAction useAutominer() {
    autominerCount--;
    return TurnAction.PLACE_AUTOMINER;
  }

  private TurnAction goCharge() {
    if (getTileTypeHere() == TileType.RECHARGE) {
      return null;
    } else {
      return findMoveActionToTile(TileType.RECHARGE);
    }
  }

  private TurnAction goSell() {
    if (isRedPlayer) {
      if (getTileTypeHere() == TileType.RED_MARKET) {
        return null;
      } else {
        return findMoveActionToTile(TileType.RED_MARKET);
      }
    } else {
      if (getTileTypeHere() == TileType.BLUE_MARKET) {
        return null;
      } else {
        return findMoveActionToTile(TileType.BLUE_MARKET);
      }
    }
  }

  private TurnAction pickupHere() {
    List<InventoryItem> itemsHere =
        currentBoard.getItemsOnGround().get(currentBoard.getYourLocation());
    if (itemsHere.contains(new InventoryItem(ItemType.AUTOMINER)) && !hasHadAutominer) {
      hasHadAutominer = true;
      autominerCount++;
      return TurnAction.PICK_UP_AUTOMINER;
    } else {
      return TurnAction.PICK_UP_RESOURCE;
    }
  }

  private TurnAction mineHere() {
    return TurnAction.MINE;
  }

  private TurnAction mineNearby() {
    int iterations = 0;
    // If preferred gem type is no longer on map, find the next best gem type that IS on the map
    while (!hasPreferredGemTileOnMap() && iterations < ItemType.values().length) {
      rotatePreferredItem();
      iterations++;
    }

    if (hasPreferredGemTileHere()) {
      return mineHere();
    } else {
      return findMoveActionToTile(preferredItem.getResourceTileType());
    }
  }

  /**
   * Switches the robot's preferred item to search for from either ruby to emerald, emerald to diamond, or
   * diamond to ruby.
   */
  private void rotatePreferredItem() {
    if (preferredItem == ItemType.RUBY || preferredItem == ItemType.AUTOMINER) {
      preferredItem = ItemType.EMERALD;
    } else if (preferredItem == ItemType.EMERALD) {
      preferredItem = ItemType.DIAMOND;
    } else {
      preferredItem = ItemType.RUBY;
    }
  }

  // ROBOT SEARCH METHODS

  public TurnAction findMoveActionToTile(TileType tileType) {
    Point closestTilePoint = findClosestTileOfTileType(tileType);

    if (getRobotLocationX() > closestTilePoint.x) {
      return TurnAction.MOVE_LEFT;                    // Robot is to the right of this point
    } else if (getRobotLocationX() < closestTilePoint.x) {
      return TurnAction.MOVE_RIGHT;                   // Robot is to the left of this point
    } else if (getRobotLocationY() > closestTilePoint.y) {
      return TurnAction.MOVE_DOWN;                    // Robot is above this point
    } else if (getRobotLocationY() < closestTilePoint.y) {
      return TurnAction.MOVE_UP;                      // Robot is below this point
    }
    return null;
  }

  /**
   * Uses a breadth-first search list to find the point on the board where the closest tile of the provided type is.
   * Returns the robot's current position if unable to find a tile.
   *
   * @param tileType the type of tile you are searching for
   * @return a Point coordinate of the location of the closest tile, or the robot's current location if not found
   */
  private Point findClosestTileOfTileType(TileType tileType) {
    Point closestTile = currentBoard.getYourLocation();
    PriorityQueue<Point> tilesToCheck = new PriorityQueue<Point>();
    HashMap<Point, Boolean> exploredTiles = new HashMap<Point, Boolean>();

    tilesToCheck.add(closestTile);
    exploredTiles.putIfAbsent(closestTile, true);
    while (!tilesToCheck.isEmpty()) {
      closestTile = tilesToCheck.poll();
      if (currentBoard.getTileTypeAtLocation(closestTile) == tileType) {
        // If tile type matches specified tile type, return its coordinate
        return closestTile;
      } else {
        // Add neighboring points if current tile is not a match
        // Upper neighbor
        Point currentNeighbor = new Point(closestTile.x, closestTile.y + 1);
        if (isPointOnBoard(currentNeighbor) && !exploredTiles.containsKey(currentNeighbor)) {
          tilesToCheck.add(currentNeighbor);  exploredTiles.putIfAbsent(currentNeighbor, true);
        }
        // Right neighbor
        currentNeighbor = new Point(closestTile.x + 1, closestTile.y);
        if (isPointOnBoard(currentNeighbor) && !exploredTiles.containsKey(currentNeighbor)) {
          tilesToCheck.add(currentNeighbor);  exploredTiles.putIfAbsent(currentNeighbor, true);
        }
        // Lower neighbor
        currentNeighbor = new Point(closestTile.x, closestTile.y - 1);
        if (isPointOnBoard(currentNeighbor) && !exploredTiles.containsKey(currentNeighbor)) {
          tilesToCheck.add(currentNeighbor);  exploredTiles.putIfAbsent(currentNeighbor, true);
        }
        // Left neighbor
        currentNeighbor = new Point(closestTile.x - 1, closestTile.y);
        if (isPointOnBoard(currentNeighbor) && !exploredTiles.containsKey(currentNeighbor)) {
          tilesToCheck.add(currentNeighbor);  exploredTiles.putIfAbsent(currentNeighbor, true);
        }
      }
    }
    return currentBoard.getYourLocation();    // If closest tile cannot be found
  }

  // ROBOT LOGIC METHODS

  /**
   * Checks if the specified list contains a gem item type.
   *
   * @param list to check for
   * @return true if list contains at least one gem, false otherwise
   */
  private boolean listContainsGem(List<InventoryItem> list) {
    return list.contains(new InventoryItem(ItemType.RUBY)) || list.contains(new InventoryItem(ItemType.EMERALD)) ||
        list.contains(new InventoryItem(ItemType.DIAMOND));
  }

  /**
   * Checks if the specified point is within the bounds of the game board.
   *
   * @param point you are checking for
   * @return true if in bounds, false if out of bounds
   */
  private boolean isPointOnBoard(Point point) {
    return (point.x >= 0) && (point.y >= 0)
        && (point.x < boardSize) && (point.y < boardSize);
  }

  /**
   * Checks various environment and robot variables to determine the robot's priority. The order of the priority
   * assignment is most important in terms of the strategy and will impact the robot's actions drastically.
   *
   * @return the enumerated RobotPriority for this turn
   */
  public RobotPriority determineRobotPriority() {
    RobotPriority priority;
    if (hasLowCharge()) {
      priority = RobotPriority.CHARGE;
    } else if (hasAutominer()) {
      priority = RobotPriority.USE_AUTOMINER;
    } else if (hasFullInventory()) {
      priority = RobotPriority.SELL;
    } else if (isGemOnGroundHere()) {
      priority = RobotPriority.PICKUP_HERE;
    } else if (hasPreferredGemTileHere()) {
      priority = RobotPriority.MINE_HERE;
    } else if (hasPreferredGemTileOnMap() || hasOtherGemTileOnMap()) {
      priority = RobotPriority.MINE_ELSEWHERE;
    } else {
      priority = RobotPriority.NULL;
    }

    return priority;
  }

  /**
   * Checks if the robot currently has an autominer in its inventory.
   *
   * @return true if robot has autominer, false otherwise
   */
  private boolean hasAutominer() {
    return autominerCount > 0;
  }

  /**
   * Checks if there is another gem tile anywhere on the map other than where the robot currently is
   *
   * @return true if there exists such a gem tile, false otherwise
   */
  private boolean hasOtherGemTileOnMap() {
    // Works by determining if the closest tile of that type is not at your current location as per the
    // method documentation in findClosestTileOfTileType()
    Point currentLocation = currentBoard.getYourLocation();
    boolean otherGemTileExistsOnMap =
        !findClosestTileOfTileType(ItemType.RUBY.getResourceTileType()).equals(currentLocation) ||
        !findClosestTileOfTileType(ItemType.DIAMOND.getResourceTileType()).equals(currentLocation) ||
        !findClosestTileOfTileType(ItemType.EMERALD.getResourceTileType()).equals(currentLocation);

    return otherGemTileExistsOnMap;
  }

  /**
   * Checks if the tile the robot is currently on is a gem tile of the robot's preferred gem type.
   *
   * @return true if the preferred gem tile is here, false otherwise
   */
  private boolean hasPreferredGemTileHere() {
    return currentBoard.getTileTypeAtLocation(currentBoard.getYourLocation()) ==
        preferredItem.getResourceTileType();
  }

  /**
   * Gets the items at the point the robot is at and checks if there is a gem item there.
   *
   * @return true if there is a gem, false otherwise
   */
  private boolean isGemOnGroundHere() {
    Map<Point, List<InventoryItem>> itemsOnGround = currentBoard.getItemsOnGround();
    List<InventoryItem> itemsHere = itemsOnGround.get(currentBoard.getYourLocation());
    return listContainsGem(itemsHere);
  }

  private boolean hasFullInventory() {
    return robotInventorySize >= maxInventorySize;
  }

  private boolean hasLowCharge() {
    return (maxRobotCharge * LOW_BATTERY_LEVEL) >= robotCharge;
  }

  /**
   * Checks if the robot's preferred gem tile type is on the map anywhere other than where the robot is.
   *
   * @return true if a gem tile type exists apart from at robot's location, false otherwise
   */
  private boolean hasPreferredGemTileOnMap() {
    return !findClosestTileOfTileType(preferredItem.getResourceTileType()).equals(currentBoard.getYourLocation()) ;
  }

  /**
   * Gets the player robot's X location on the board
   *
   * @return an int representing the x coordinate
   */
  private int getRobotLocationX() {
    return currentBoard.getYourLocation().x;
  }

  /**
   * Gets the player robot's Y location on the board
   *
   * @return an int representing the y coordinate
   */
  private int getRobotLocationY() {
    return currentBoard.getYourLocation().y;
  }

  // ROBOT EVENT METHODS

  /**
   * Updates the robot's data values based on new turn's information.
   *
   * @param boardView the current turn's board layout
   * @param currentCharge the charge on the robot this turn
   */
  private void updateRobot(PlayerBoardView boardView, int currentCharge) {
    this.currentBoard = boardView;
    this.robotCharge = currentCharge;
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
    if (itemReceived.getItemType() == ItemType.AUTOMINER) {
      autominerCount++;
    }
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
   * Resets the robot's non-initializable values at the end of a round, when one player wins
   *
   * @param pointsScored The total number of points this strategy scored
   * @param opponentPointsScored The total number of points the opponent's strategy scored
   */
  @java.lang.Override
  public void endRound(int pointsScored, int opponentPointsScored) {
    resetRobot();
    autominerCount = 0;
  }

  // ROBOT OTHER METHODS

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
   * Where the robot data for values not initialized with new round values will be reset to default values
   */
  private void resetRobot() {
    robotInventorySize = 0;
    preferredItem = ItemType.DIAMOND;
    hasHadAutominer = false;
  }

  private TileType getTileTypeHere() {
    return currentBoard.getTileTypeAtLocation(currentBoard.getYourLocation());
  }
}
