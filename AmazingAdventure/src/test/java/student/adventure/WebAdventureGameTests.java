package student.adventure;

import com.google.gson.Gson;
import maplayout.MapLayout;
import org.junit.Before;
import org.junit.Test;
import static student.adventure.AdventureGame.*;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class WebAdventureGameTests {
  static final String pathname = "src/main/resources/uiuc_squirrel_game.json";

  Gson gson;
  Reader reader;
  MapLayout mapLayout;
  WebAdventureGame game;

  @Before
  public void setUp() {
    // This is run before every test.
    gson = new Gson();

    try {
      reader = Files.newBufferedReader(Paths.get(pathname));
    } catch (Exception e) {
      System.out.println("Unable to open JSON file in path " + pathname);
      return;
    }

    mapLayout = gson.fromJson(reader, MapLayout.class);
    game = new WebAdventureGame(mapLayout);
  }

  @Test
  /**
   * Tests to see if the correct text is displayed on clicking the game link, before any turns are performed.
   */
  public void testStartScreenMessage() {
    String expected = "";
    expected += mapLayout.getStartMessage();
    expected += System.lineSeparator();
    expected = expected.concat("PLAYER COMMANDS:");
    expected = expected.concat(System.lineSeparator());
    expected =
        expected.concat(EXIT_COMMAND + ", " + QUIT_COMMAND + ", " + TAKE_COMMAND + " <item>, " + DROP_COMMAND + " "
        + "<item>, " + GO_COMMAND + " <direction>, " + USE_COMMAND + " <item>, " + PATH_COMMAND);
    expected = expected.concat(System.lineSeparator());
    expected = expected.concat("Press button to start game!");
    expected = expected.concat(System.lineSeparator());

    assertEquals(expected, game.getGameStatusMessage());
  }

  @Test
  /**
   * Checks if sending the game the 'go' command outputs the correct status message.
   */
  public void testGoTurn() {
    game.performTurn("go east");
    String expected = "You did not go in a valid direction and ran into a squirrel roadblock!\n";
    if (game.getCurrentInjuryLevel() == 1) {
      expected += "You get mauled by a squirrel!\n";
    } else {
      expected += "You managed to avoid the squirrels, for now.\n";
    }
    expected += game.getSituationMessage();

    assertEquals(expected, game.getGameStatusMessage());
  }

  @Test
  /**
   * Makes sure only the correct exit text is displayed to the player when they send the exit command.
   */
  public void testExitTurn() {
    game.performTurn("exit");
    String expected = "Exiting game...\n";
    expected += mapLayout.getDeathMessage();
    expected += System.lineSeparator();

    assertEquals(expected, game.getGameStatusMessage());
  }

  @Test
  /**
   * Checks that the appropriate status message is output after a player tries to take an item in the starting area.
   */
  public void testTakeTurn() {
    game.performTurn("take");
    String expected = "There is nothing new to pick up here!\n";
    if (game.getCurrentInjuryLevel() == 1) {
      expected += "You get mauled by a squirrel!\n";
    } else {
      expected += "You managed to avoid the squirrels, for now.\n";
    }
    expected += game.getSituationMessage();

    assertEquals(expected, game.getGameStatusMessage());
  }

  @Test
  /**
   * Checks that the appropriate status message is output after a player tries to use an item in the starting area.
   */
  public void testUseTurn() {
    game.performTurn("take");
    String expected = "There is nothing new to pick up here!\n";
    if (game.getCurrentInjuryLevel() == 1) {
      expected += "You get mauled by a squirrel!\n";
    } else {
      expected += "You managed to avoid the squirrels, for now.\n";
    }
    expected += game.getSituationMessage();

    assertEquals(expected, game.getGameStatusMessage());
  }

  @Test
  /**
   * Checks that the appropriate status message is output after a player tries to use an item in the starting area.
   */
  public void testInvalidTurn() {
    String invalidCommand = "foo";
    game.performTurn(invalidCommand);
    String expected = "You ponder what it means to '" + invalidCommand + "'.\n";
    if (game.getCurrentInjuryLevel() == 1) {
      expected += "You get mauled by a squirrel!\n";
    } else {
      expected += "You managed to avoid the squirrels, for now.\n";
    }
    expected += game.getSituationMessage();

    assertEquals(expected, game.getGameStatusMessage());
  }
}
