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

  private final Hashtable<Integer, WebAdventureGame> gameInstances;
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
    WebAdventureGame game = new WebAdventureGame(layout);
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
    WebAdventureGame gameInstance = gameInstances.get(id);
    GameStatus gameStatus = new GameStatus(false, id, gameInstance.getGameStatusMessage(), "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/The_Sun_by_the_Atmospheric_Imaging_Assembly_of_NASA%27s_Solar_Dynamics_Observatory_-_20100819.jpg/220px-The_Sun_by_the_Atmospheric_Imaging_Assembly_of_NASA%27s_Solar_Dynamics_Observatory_-_20100819.jpg", "",
        new AdventureState(), gameInstance.getCommandOptions());
    return gameStatus;
  }

  @Override
  public boolean destroyGame(int id) {
    AdventureGame removedGame = gameInstances.remove(id);
    return removedGame != null;
  }

  @Override
  public void executeCommand(int id, Command command) {
    WebAdventureGame gameInstance = gameInstances.get(id);
    String fullCommand = command.getCommandName() + " " + command.getCommandValue();
    gameInstance.performTurn(fullCommand);
  }

  @Override
  public SortedMap<String, Integer> fetchLeaderboard() {
    return null;
  }
}
