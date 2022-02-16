

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.ItemType;
import mineopoly_three.strategy.MinePlayerStrategy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.strategy.AssignmentStrategy;
import mineopoly_three.strategy.PlayerBoardView;
import mineopoly_three.tiles.TileType;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AssignmentStrategyTest {
  private static PlayerBoardView playerBoardView;
  private static AssignmentStrategy playerStrategy;

  // Default values for building AssignmentStrategy object
  private static final int boardSize = 5;
  private static final int maxInventorySize = 5;
  private static final int maxCharge = 20;
  private static final boolean isRedPlayer = true;
  private static Map<Point, List<InventoryItem>> itemsOnGround;
  private static final Point thisPlayerLocation = new Point(1, 1);;
  private static final int winningScore = 200;       // Irrelevant values
  private static final int otherPlayerScore = 0;     // Irrelevant values
  private static final Random random = new Random(); // Irrelevant values
  private static final Point otherPlayerLocation = new Point(4, 1);    // Irrelevant values
  private static final Economy economy = new Economy(new ItemType[]{ItemType.RUBY, ItemType.EMERALD, ItemType.DIAMOND});
  private static final Point RUBY_LOCATION = new Point(0, 1);
  private static final Point AUTOMINER_LOCATION = new Point(4, 0);

  // VERY IMPORTANT - The actual testing map
  private static final TileType[][] tiles = {
      {TileType.EMPTY, TileType.RESOURCE_EMERALD, TileType.EMPTY, TileType.RESOURCE_DIAMOND, TileType.EMPTY},
      {TileType.EMPTY, TileType.RESOURCE_EMERALD, TileType.RECHARGE, TileType.EMPTY, TileType.RESOURCE_DIAMOND},
      {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.RESOURCE_RUBY, TileType.EMPTY},
      {TileType.EMPTY, TileType.RED_MARKET, TileType.RESOURCE_RUBY, TileType.RESOURCE_RUBY, TileType.BLUE_MARKET},
      {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY}
  };

  // Code to initialize items on the ground of game
  public enum MapItemInitFlag { JUST_AUTOMINER, RUBY_ON_GROUND_PLUS_AUTOMINER }
  private Map<Point, List<InventoryItem>> initializeItemMap(MapItemInitFlag flag) {
    Map<Point, List<InventoryItem>> itemsOnGround = new HashMap<>();
    switch (flag) {
      case RUBY_ON_GROUND_PLUS_AUTOMINER:
        itemsOnGround.putIfAbsent(RUBY_LOCATION, new ArrayList<>(List.of(new InventoryItem(ItemType.RUBY))));
      case JUST_AUTOMINER:
        itemsOnGround.putIfAbsent(AUTOMINER_LOCATION, new ArrayList<>(List.of(new InventoryItem(ItemType.AUTOMINER))));
        break;
    }
    return itemsOnGround;
  }

  @Before
  public void setUp() {
    // This is run before every test.
    playerStrategy = new AssignmentStrategy();
    itemsOnGround = initializeItemMap(MapItemInitFlag.JUST_AUTOMINER);
  }

  @Test
  // Tests if robot will start to go charge when on low battery
  public void testGetTurnActionCharge() {
    // Set Up
    int maxCharge = 0;
    playerBoardView = new PlayerBoardView(tiles, itemsOnGround, thisPlayerLocation, otherPlayerLocation, 0);
    playerStrategy.initialize(boardSize, maxInventorySize, maxCharge, winningScore, playerBoardView,
        thisPlayerLocation, isRedPlayer, random);
    TurnAction actual = playerStrategy.getTurnAction(playerBoardView, economy, maxCharge, true);

    assertEquals(actual, TurnAction.MOVE_RIGHT);
  }

  @Test
  // Tests if the robot will start to go sell when it has a full inventory and good charge
  public void testGetTurnActionSell() {
    // Set Up
    Point thisPlayerLocation = new Point(1, 2);
    int maxInventorySize = 0;
    playerBoardView = new PlayerBoardView(tiles, itemsOnGround, thisPlayerLocation, otherPlayerLocation, 0);
    playerStrategy.initialize(boardSize, maxInventorySize, maxCharge, winningScore, playerBoardView,
        thisPlayerLocation, isRedPlayer, random);
    TurnAction actual = playerStrategy.getTurnAction(playerBoardView, economy, maxCharge, true);

    assertEquals(actual, TurnAction.MOVE_DOWN);
  }

  @Test
  // Tests if the robot will pick up an item when it has good charge, empty space in inventory, and on tile with item
  public void testGetTurnActionPickupHere() {
    // Set Up
    Point thisPlayerLocation = RUBY_LOCATION;
    itemsOnGround = initializeItemMap(MapItemInitFlag.RUBY_ON_GROUND_PLUS_AUTOMINER);
    playerBoardView = new PlayerBoardView(tiles, itemsOnGround, thisPlayerLocation, otherPlayerLocation, 0);
    playerStrategy.initialize(boardSize, maxInventorySize, maxCharge, winningScore, playerBoardView,
        thisPlayerLocation, isRedPlayer, random);
    TurnAction actual = playerStrategy.getTurnAction(playerBoardView, economy, maxCharge, true);

    assertEquals(actual, TurnAction.PICK_UP_RESOURCE);
  }

  @Test
  // Tests if the robot will mine when it has good charge, empty space in inventory, and on preferred
  // gem tile
  public void testGetTurnActionMineHere() {
    // Set Up
    Point thisPlayerLocation = new Point(3, 4);
    itemsOnGround = initializeItemMap(MapItemInitFlag.JUST_AUTOMINER);
    playerBoardView = new PlayerBoardView(tiles, itemsOnGround, thisPlayerLocation, otherPlayerLocation, 0);
    playerStrategy.initialize(boardSize, maxInventorySize, maxCharge, winningScore, playerBoardView,
        thisPlayerLocation, isRedPlayer, random);
    TurnAction actual = playerStrategy.getTurnAction(playerBoardView, economy, maxCharge, true);

    assertEquals(actual, TurnAction.MINE);
  }

  @Test
  // Tests if the robot will move towards preferred gem tile when it has good charge, empty space in inventory, and
  // near a preferred gem tile on map
  public void testGetTurnActionMineElsewhere() {
    // Set Up
    Point thisPlayerLocation = new Point(3, 3);
    itemsOnGround = initializeItemMap(MapItemInitFlag.JUST_AUTOMINER);
    playerBoardView = new PlayerBoardView(tiles, itemsOnGround, thisPlayerLocation, otherPlayerLocation, 0);
    playerStrategy.initialize(boardSize, maxInventorySize, maxCharge, winningScore, playerBoardView,
        thisPlayerLocation, isRedPlayer, random);
    TurnAction actual = playerStrategy.getTurnAction(playerBoardView, economy, maxCharge, true);

    assertEquals(actual, TurnAction.MOVE_UP);
  }

  @Test
  // Tests if the robot's inventory grows after receiving an item
  public void testOnReceiveItem() {
    // Set Up
    itemsOnGround = initializeItemMap(MapItemInitFlag.JUST_AUTOMINER);
    playerBoardView = new PlayerBoardView(tiles, itemsOnGround, thisPlayerLocation, otherPlayerLocation, 0);
    playerStrategy.initialize(boardSize, maxInventorySize, maxCharge, winningScore, playerBoardView,
        thisPlayerLocation, isRedPlayer, random);
    TurnAction actual = playerStrategy.getTurnAction(playerBoardView, economy, maxCharge, true);

    // Receive item check
    int previousInventory = playerStrategy.getRobotInventorySize();
    playerStrategy.onReceiveItem(new InventoryItem(ItemType.RUBY));
    int actualInventory = playerStrategy.getRobotInventorySize();

    assertEquals(actualInventory, previousInventory + 1);
  }

  @Test
  // Tests if the robot's preferred gem type rotates after selling inventory
  public void testOnSoldInventory() {
    // Set Up
    itemsOnGround = initializeItemMap(MapItemInitFlag.JUST_AUTOMINER);
    playerBoardView = new PlayerBoardView(tiles, itemsOnGround, thisPlayerLocation, otherPlayerLocation, 0);
    playerStrategy.initialize(boardSize, maxInventorySize, maxCharge, winningScore, playerBoardView,
        thisPlayerLocation, isRedPlayer, random);
    TurnAction actual = playerStrategy.getTurnAction(playerBoardView, economy, maxCharge, true);

    // Sell items check
    playerStrategy.onSoldInventory(100);
    ItemType newPreferredItem = playerStrategy.getPreferredItem();

    assertEquals(newPreferredItem, ItemType.RUBY);
  }

  @Test
  // Tests if values like inventorySize of robot resets to zero after endRound()
  public void testEndRound() {
    // Set Up
    Point thisPlayerLocation = RUBY_LOCATION;
    itemsOnGround = initializeItemMap(MapItemInitFlag.RUBY_ON_GROUND_PLUS_AUTOMINER);
    playerBoardView = new PlayerBoardView(tiles, itemsOnGround, thisPlayerLocation, otherPlayerLocation, 0);
    playerStrategy.initialize(boardSize, maxInventorySize, maxCharge, winningScore, playerBoardView,
        thisPlayerLocation, isRedPlayer, random);
    // Robot picks up item
    TurnAction actual = playerStrategy.getTurnAction(playerBoardView, economy, maxCharge, true);
    playerStrategy.endRound(winningScore, winningScore);
    int actualInventorySize = playerStrategy.getRobotInventorySize();

    assertEquals(actualInventorySize, 0);
  }
}