package maplayout;

import java.util.List;

public class MapArea {
  // There must be a MapArea with a zero id and all other ids must be non-negative
  int areaId;

  // Holds the setting information as well as the possible paths that can be taken to be displayed to the user
  String description;

  // Describes the danger the area poses to the player via squirrels (value between 0 and 10)
  int initialThreatLevel;

  // Describes the id of the item that can be picked up by a player using the "take" command
  int itemInArea;

  // A list of directions which can be taken by the player using the "go" command in-game
  List<Direction> directions;

  // GETTER METHODS

  public int getAreaId() {
    return areaId;
  }

  public String getDescription() {
    return description;
  }

  public int getInitialThreatLevel() {
    return initialThreatLevel;
  }

  public int getItemInArea() {
    return itemInArea;
  }

  public List<Direction> getDirections() {
    return directions;
  }

  // OTHER METHODS

  /**
   * Checks if this MapArea allows travel in the specified direction and returns the next areas id
   *
   * @param directionName the name of the direction you are checking for the existence of
   * @return the id of the next MapArea if the direction is found, -1 if not found
   */
  public int findDirection(String directionName) {
    for (Direction direction : directions) {
      if (direction.getDirectionName().equalsIgnoreCase(directionName)) {
        return direction.getNextArea();
      }
    }

    return -1;
  }
}
