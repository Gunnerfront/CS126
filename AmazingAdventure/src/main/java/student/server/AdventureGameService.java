package student.server;

import java.util.SortedMap;

public class AdventureGameService implements AdventureService {

  @Override
  public void reset() {

  }

  @Override
  public int newGame() throws AdventureException {
    return 0;
  }

  @Override
  public GameStatus getGame(int id) {
    return null;
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
