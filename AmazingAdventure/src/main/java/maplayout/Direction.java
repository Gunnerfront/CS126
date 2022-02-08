package maplayout;

public class Direction {
  // Either "NORTH," "SOUTH," "EAST," or "WEST"
  String directionName;

  // Describes the id of the area this direction leads to
  int nextArea;

  // GETTER METHODS

  public String getDirectionName() {
    return directionName;
  }

  public int getNextArea() {
    return nextArea;
  }
}
