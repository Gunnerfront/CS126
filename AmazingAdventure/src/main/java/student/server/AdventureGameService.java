package student.server;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.Gson;
import maplayout.MapLayout;
import student.adventure.AdventureGame;
import student.adventure.WebAdventureGame;

public class AdventureGameService implements AdventureService {

  private static final String MAP_LAYOUT_JSON_FILEPATH = "src/main/resources/uiuc_squirrel_game.json";
  private static final String imageUrl = "https://www.nationstates.net/images/flags/uploads/ultimate_squirrel_army__759704.jpg";

  private final Hashtable<Integer, WebAdventureGame> gameInstances;
  private static int currentInstanceId;

  public AdventureGameService() {
    this.gameInstances = new Hashtable<>();
    this.currentInstanceId = 0;
  }

  @Override
  /**
   * Resets the service to its initial state, including the instance id classifier.
   */
  public void reset() {
    gameInstances.clear();
    currentInstanceId = 0;
  }

  @Override
  /**
   * Creates a new Adventure game and stores it.
   *
   * @return the id of the game that was created
   */
  public int newGame() throws AdventureException {
    MapLayout layout = extractMapLayout();
    WebAdventureGame game = new WebAdventureGame(layout);
    gameInstances.put(currentInstanceId, game);
    return currentInstanceId++;
  }

  /**
   * Reads a JSON file to extract the game map information for the AdventureGame session.
   *
   * @return the MapLayout extracted from the JSON file
   * @throws AdventureException if the JSON file could not be opened
   */
  private MapLayout extractMapLayout() throws  AdventureException {
    Gson gson = new Gson();
    Reader reader;

    // Check if JSON file could be opened
    try {
      reader = Files.newBufferedReader(Paths.get(MAP_LAYOUT_JSON_FILEPATH));
    } catch (Exception e) {
      throw new AdventureException("JSON file for map layout could not be read.");
    }

    MapLayout mapLayout = gson.fromJson(reader, MapLayout.class);
    return  mapLayout;
  }

  @Override
  /**
   * Returns the state of the game instance associated with the given ID.
   *
   * @param id the instance id
   * @return the current state of the game
   */
  public GameStatus getGame(int id) {
    WebAdventureGame gameInstance = gameInstances.get(id);
    GameStatus gameStatus = new GameStatus(false, id, gameInstance.getGameStatusMessage(), imageUrl,
        "", new AdventureState(), gameInstance.getCommandOptions());
    return gameStatus;
  }

  @Override
  /**
   * Removes & destroys a game instance with the given ID.
   *
   * @param id the instance id
   * @return false if the instance could not be found and/or was not deleted
   */
  public boolean destroyGame(int id) {
    AdventureGame removedGame = gameInstances.remove(id);
    return removedGame != null;
  }

  @Override
  /**
   * Executes a command on the game instance with the given id, changing the game state if applicable.
   *
   * @param id the instance id
   * @param command the issued command
   */
  public void executeCommand(int id, Command command) {
    WebAdventureGame gameInstance = gameInstances.get(id);
    String fullCommand = command.getCommandName() + " " + command.getCommandValue();
    gameInstance.performTurn(fullCommand);
  }

  @Override
  /**
   * Not implemented for this game and returns null.
   *
   * @return null
   */
  public SortedMap<String, Integer> fetchLeaderboard() {
    return null;
  }
}
