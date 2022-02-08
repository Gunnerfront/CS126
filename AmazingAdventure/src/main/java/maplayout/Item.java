package maplayout;

public class Item {

  // Can be 0 for no item, 1 for Baseball Bat, 2 for MedKit, or 3 for Bus Key based on JSON file
  int itemId;

  // A brief description of the item, including what its purpose is in the game and when it can be used
  String itemDescription;

  // GETTER METHODS

  public int getItemId() {
    return itemId;
  }

  public String getItemDescription() {
    return itemDescription;
  }

  @Override
  public String toString() {
    return itemDescription;
  }
}
