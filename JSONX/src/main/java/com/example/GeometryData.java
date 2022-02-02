package com.example;

import com.google.gson.annotations.SerializedName;

public class GeometryData {
  // Geometry (or Location) data of a historical feature as found in the JSON file at
  // https://catalog.data.gov/dataset/gnis-historic-features

  // I don't actually use this class other than for parsing, so no need for methods
  @SerializedName("type")
  private String typeOfData;          // Usually just has value "Point"

  @SerializedName("DATE_CREAT")
  private Double[] coordinates;       // Coordinates, if known; [0.0, 0.0] if unknown location


}
