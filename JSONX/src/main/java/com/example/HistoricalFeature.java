package com.example;

public class HistoricalFeature {
  // The three parts of a Historical Feature from the JSON dataset I found at:
  // https://catalog.data.gov/dataset/gnis-historic-features

  // Each historical feature in the JSON datafile was divided into 3 parts:

  // Represents the coordinates of the feature as a point
  // (Holds redundant data that also appears in PropertyData class)
  private GeometryData geometryData;

  // The type of the feature (appears as "Feature" in every entry, so it is very unnecessary)
  private String type;

  // Holds a bunch of values such as the feature name and locations
  private PropertyData propertyData;

  // I only need to "get" PropertyData for this assignment as the other
  // classes hold unnecessary data

  /**
   * Getter for the feature's property data which is a class that holds the
   * values for various data points such as the feature's location and name.
   *
   * @return a PropertyData object that holds the feature's property data
   *         which can be accessed via additional getter methods
   */
  public PropertyData getPropertyData() {
    return propertyData;
  }

  public GeometryData getGeometryData() { return geometryData;}
}
