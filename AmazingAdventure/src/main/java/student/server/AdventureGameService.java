package student.server;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.Gson;
import maplayout.MapLayout;
import student.adventure.AdventureGame;

public class AdventureGameService implements AdventureService {

  private static final String MAP_LAYOUT_JSON_FILEPATH = "src/main/resources/uiuc_squirrel_game.json";

  private final Hashtable<Integer, AdventureGame> gameInstances;
  private static int currentInstanceId;

  public AdventureGameService() {
    this.gameInstances = new Hashtable<>();
    this.currentInstanceId = 0;
  }

  @Override
  public void reset() {
    gameInstances.clear();
    currentInstanceId = 0;
  }

  @Override
  public int newGame() throws AdventureException {
    MapLayout layout = extractMapLayout();
    AdventureGame game = new AdventureGame(layout, true);
    gameInstances.put(currentInstanceId, game);
    return currentInstanceId++;
  }

  private MapLayout extractMapLayout() throws  AdventureException {
    Gson gson = new Gson();
    final String pathname = "src/main/resources/uiuc_squirrel_game.json";
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
  public GameStatus getGame(int id) {
    AdventureGame gameInstance = gameInstances.get(id);
    GameStatus gameStatus = new GameStatus(false, id, gameInstance.getGameStatusMessage(), "", "",
        new AdventureState(), gameInstance.getCommandOptions());
    return gameStatus;
  }

  @Override
  public boolean destroyGame(int id) {
    return false;
  }

  @Override
  public void executeCommand(int id, Command command) {

  }

  @Override
  public SortedMap<String, Integer> fetchLeaderboard() {
    return null;
  }
}
