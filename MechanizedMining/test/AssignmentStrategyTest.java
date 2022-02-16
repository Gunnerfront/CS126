

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.ItemType;
import mineopoly_three.strategy.MinePlayerStrategy;
import org.junit.Assert.*;

import mineopoly_three.item.InventoryItem;
import mineopoly_three.strategy.AssignmentStrategy;
import mineopoly_three.strategy.PlayerBoardView;
import mineopoly_three.tiles.TileType;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AssignmentStrategyTest {
  private static PlayerBoardView playerBoardView;
  private static MinePlayerStrategy playerStrategy;

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
        itemsOnGround.putIfAbsent(new Point(0, 1), new ArrayList<>(List.of(new InventoryItem(ItemType.RUBY))));
      case JUST_AUTOMINER:
        itemsOnGround.putIfAbsent(new Point(4, 0), new ArrayList<>(List.of(new InventoryItem(ItemType.AUTOMINER))));
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
  // Checks that the Direction class has filled values
  public void testGetTurnActionCharge() {
    // Set Up
    playerBoardView = new PlayerBoardView(tiles, itemsOnGround, thisPlayerLocation, otherPlayerLocation, 0);
    playerStrategy.initialize(boardSize, maxInventorySize, maxCharge, winningScore, playerBoardView,
        thisPlayerLocation, isRedPlayer, random);
    TurnAction actual = playerStrategy.getTurnAction(playerBoardView, economy, 0, true);

  }
}