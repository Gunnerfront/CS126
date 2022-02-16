package mineopoly_three.strategy;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.tiles.TileType;
import mineopoly_three.game.GameBoard;
import mineopoly_three.*;

import java.awt.Point;
import java.util.*;

public class AssignmentStrategy implements MinePlayerStrategy {
  private static final String PLAYER_NAME = "Gunnerside";

  // The distance that the robot considers as "nearby" when searching for nearby items
  private static final int NEARBY_DISTANCE = 5;

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
      case SELL -> goSell();
      case PICKUP_HERE -> pickupHere();
      case PICKUP_ELSEWHERE -> pickupElsewhere();
      case MINE_HERE -> mineHere();
      case MINE_NEARBY -> mineNearby();
      default -> null;
    };

    return action;
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
    return TurnAction.PICK_UP_RESOURCE;
  }

  private  TurnAction pickupElsewhere() {
    return findMoveActionToItem(null);
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
    return findMoveActionToTile(preferredItem.getResourceTileType());
  }

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

  private TurnAction findMoveActionToItem(ItemType itemType) {
    Point closestTilePoint = findClosestTileWithItem(itemType);

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
   *
   * @param tileType the type of tile you are searching for
   * @return a Point coordinate of the location of the closest tile, or the robot's current location if not found
   */
  private Point findClosestTileOfTileType(TileType tileType) {
    Point startPoint = new Point(getRobotLocationX(), getRobotLocationY());
    List<Point> searchList = getBoardSearchList(startPoint);
    for (Point point : searchList) {
      if (currentBoard.getTileTypeAtLocation(point) == tileType) {
        return point;
      }
    }
    return startPoint;
  }

  /**
   * Searches all items on the ground of the current game board for the nearest item of the specified type.
   *
   * @param itemType the type of the item you are looking for
   * @return the Point coordinate of the item, or the robot's current location if not found
   */
  private Point findClosestTileWithItem(ItemType itemType) {
    Point closestPoint = new Point(getRobotLocationX(), getRobotLocationY());
    Point robotPoint = new Point(getRobotLocationX(), getRobotLocationY());
    Map<Point, List<InventoryItem>> itemsSearchMap = currentBoard.getItemsOnGround();

    // Searches all points on map
    for (Point point : itemsSearchMap.keySet()) {
      // Checks if this point has that item there
      if (itemType == null || itemsSearchMap.get(point).contains(new InventoryItem(itemType))) {
        // Checks if this point is closer than current closest point found
        if (distanceBetweenPoints(robotPoint, point) < distanceBetweenPoints(robotPoint, closestPoint)) {
          closestPoint = point;
        }
      }
    }

    return closestPoint;
  }

  /**
   * Returns the non-Euclidean distance between two points. Ex: The distance between (3, 4) and (5, 6)
   * is (5 - 3) + (6 - 4) = 4.
   *
   * @param point1 one point
   * @param point2 another point
   * @return the integer distance between the points
   */
  private int distanceBetweenPoints(Point point1, Point point2) {
    int xDistance = Math.abs(point1.x - point2.x);
    int yDistance = Math.abs(point1.y - point2.y);
    return xDistance + yDistance;
  }

  /**
   * Uses breadth-first search to create a list of points for robot to search starting from closest points to farthest.
   *
   * @param start Point from where BFS begins
   * @return  a List of Point objects in BFS order from the specified point
   */
  private List<Point> getBoardSearchList(Point start) {
    List<Point> pointsToSearch = new ArrayList<>();   // return list
    // Start of BFS
    Point currentPoint = start;
    HashMap<Point, Boolean> exploredPoints = new HashMap<Point, Boolean>();
    PriorityQueue<Point> pointsToAdd = new PriorityQueue<Point>();
    pointsToAdd.add(currentPoint);
    exploredPoints.putIfAbsent(currentPoint, true);

    while (!pointsToAdd.isEmpty()) {
        currentPoint = pointsToAdd.poll();
        pointsToSearch.add(currentPoint);
        // Add neighboring points if current tile is not a match
        // Upper neighbor
        Point currentNeighbor = new Point(currentPoint.x, currentPoint.y + 1);
        if (ifPointIsOnBoard(currentNeighbor) && !exploredPoints.containsKey(currentNeighbor)) {
          pointsToAdd.add(currentNeighbor);  exploredPoints.putIfAbsent(currentNeighbor, true);
        }
        // Right neighbor
        currentNeighbor = new Point(currentPoint.x + 1, currentPoint.y);
        if (ifPointIsOnBoard(currentNeighbor) && !exploredPoints.containsKey(currentNeighbor)) {
          pointsToAdd.add(currentNeighbor);  exploredPoints.putIfAbsent(currentNeighbor, true);
        }
        // Lower neighbor
        currentNeighbor = new Point(currentPoint.x, currentPoint.y - 1);
        if (ifPointIsOnBoard(currentNeighbor) && !exploredPoints.containsKey(currentNeighbor)) {
          pointsToAdd.add(currentNeighbor);  exploredPoints.putIfAbsent(currentNeighbor, true);
        }
        // Left neighbor
        currentNeighbor = new Point(currentPoint.x - 1, currentPoint.y);
        if (ifPointIsOnBoard(currentNeighbor) && !exploredPoints.containsKey(currentNeighbor)) {
          pointsToAdd.add(currentNeighbor);  exploredPoints.putIfAbsent(currentNeighbor, true);
        }
      }

    return pointsToSearch;
  }

  private boolean ifPointIsOnBoard(Point point) {
    return (point.x >= 0) && (point.y >= 0)
        && (point.x < boardSize) && (point.y < boardSize);
  }

  private void rotatePreferredItem() {
    // TODO
  }

  /**
   * Checks various environment and robot variables to determine the robot's priority. The order of the priority
   * assignment is most important in terms of the strategy and will impact the robot's actions drastically.
   *
   * @return
   */
  public RobotPriority determineRobotPriority() {
    RobotPriority priority;
    if (hasLowCharge()) {
      priority = RobotPriority.CHARGE;
    } else if (hasFullInventory()) {
      priority = RobotPriority.SELL;
    } else if (isGemOnGroundHere()) {
      priority = RobotPriority.PICKUP_HERE;
    } else if (isGemOnGroundOnMap()) {
      priority = RobotPriority.PICKUP_ELSEWHERE;
    } else if (hasPreferredGemTileHere()) {
      priority = RobotPriority.MINE_HERE;
    } else if (hasPreferredGemTileOnMap() || hasOtherGemTileOnMap()) {
      priority = RobotPriority.MINE_NEARBY;
    } else {
      priority = RobotPriority.NULL;
    }

    return priority;
  }

  private boolean hasOtherGemTileOnMap() {
    // TODO
  }

  private boolean hasPreferredGemTileHere() {
    // TODO
  }

  private boolean isGemOnGroundOnMap() {
    // TODO
  }

  private boolean isGemOnGroundHere() {
    // TODO
  }

  private boolean hasFullInventory() {
    // TODO
  }

  private boolean hasLowCharge() {
    // TODO
  }

  private boolean hasPreferredGemTileOnMap() {
    // TODO
  }

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

  private TileType getTileTypeHere() {
    return currentBoard.getTileTypeAtLocation(getRobotLocationX(), getRobotLocationY());
  }
}
