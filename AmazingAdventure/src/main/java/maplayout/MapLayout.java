package maplayout;

import java.util.List;

public class MapLayout {
  // The message that is displayed to the user as the game starts
  String startMessage;

  // The message that is displayed when the player dies to the squirrels
  String deathMessage;

  // The id of the first area, typically zero
  int startAreaId;

  // The id of the final area representing the win condition
  int endAreaId;

  // A list of every type of item in the game, with each item having an id and a description
  List<Item> itemTypes;

  // A list of every area in the game, with each area having numerous attributes such as a description,
  // threat level, items, etc.
  List<MapArea> areas;

  // GETTER METHODS

  public String getStartMessage() {
    return startMessage;
  }

  public String getDeathMessage() {
    return deathMessage;
  }

  public int getStartAreaId() {
    return startAreaId;
  }

  public int getEndAreaId() {
    return endAreaId;
  }

  public List<Item> getItemTypes() {
    return itemTypes;
  }

  public List<MapArea> getAreas() {
    return areas;
  }

  // OTHER METHODS

  /**
   * Searches for the item description of the item with the specified id.
   *
   * @param itemId id of the item whose description you want
   * @return the description of the item if found, empty string if not found
   */
  public String findItemDescription(int itemId) {
    for (Item item : itemTypes) {
      if (item.getItemId() == itemId) {
        return item.getItemDescription();
      }
    }

    return "";
  }

  /**
   * Finds the MapArea in the list of MapAreas with the specified areaId. Requires existence of a MapArea
   * with areaId of zero, else might cause a recursive infinite loop.
   *
   * @param targetId the id of the MapArea you are looking for
   * @return the mapArea with the specified areaId
   */
  public MapArea findMapArea(int targetId) {
    for (MapArea mapArea : areas) {
      if (mapArea.getAreaId() == targetId) {
        return mapArea;
      }
    }

    return findMapArea(0);
  }
}
