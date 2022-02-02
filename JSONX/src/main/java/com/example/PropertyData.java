package com.example;

import com.google.gson.annotations.SerializedName;

public class PropertyData {
  // Properties of a historical feature as found in the JSON file at
  // https://catalog.data.gov/dataset/gnis-historic-features
  // Some of these data points I could not figure out the purpose of, even on the website,
  // so I unfortunately could not provide documentation about their purpose

  // Properties with a *** are properties I actually use
  @SerializedName("DATE_CREAT")
  private String dateCreated;               // *** Date Feature was Added
  @SerializedName("PRIM_LONG1")
  private Double longitudeAsNumber;         // *** Longitude as a number, as appears in coordinates of GeometryData
  @SerializedName("STATE_ALPH")
  private String stateAbbreviation;         // *** Ex: "NM" for New Mexico
  @SerializedName("ELEVATION")
  private Double elevation;                 // *** Elevation in meters of the feature
  @SerializedName("PRIMARY_LA")
  private String primaryLatitudeAsString;   // Latitude in format: {amount}{N or S}
  @SerializedName("FEATURE_CL")
  private String featureClassifier;         // *** Type of feature (i.e. Populated Place, Post Office, etc.)
  @SerializedName("observed")
  private String observed;                  // Disregard: Unknown Purpose
  @SerializedName("SOURCE_LAT")
  private String sourceLatitude;            // Disregard: Unknown Purpose
  @SerializedName("FEATURE_NA")
  private String featureName;               // *** Name of the feature
  @SerializedName("PRIM_LONG_")
  private String primaryLongitudeAsString;  // Longitude in format: {amount}{E or W}
  @SerializedName("id")
  private long id;
  @SerializedName("FEATURE_ID")
  private long featureId;                   // *** The id of the actual feature
  @SerializedName("SOURCE_L_2")
  private String sourceLatitude2;           // Disregard: Unknown Purpose
  @SerializedName("STATE_NUME")
  private int stateId;                      // ID of the state the feature is in
  @SerializedName("MAP_NAME")
  private String mapName;                   // Name of the map the feature appears in
  @SerializedName("COUNTY_NAM")
  private String countyName;                // *** Name of the county feature is in
  @SerializedName("DATE_EDITE")
  private String dateEdited;                // Last date the feature data was edited
  @SerializedName("SOURCE_LON")
  private String sourceLongitude;           // Disregard: Unknown Purpose
  @SerializedName("COUNTY_NUM")
  private int countyNumber;                 // The county the feature is in represented as a number
  @SerializedName("SOURCE_L_1")
  private String  sourceL1;                 // Disregard: Unknown Purpose
  @SerializedName("PRIM_LAT_D")
  private Double latitudeAsNumber;          // *** Latitude as a number, as appears in coordinates of GeometryData

  // I defined getters only for those properties I actually use:

  /**
   * Getter for dateCreated, the date feature was added formatted as YEAR/MONTH/DAY
   *
   * @return the Date Feature was Added as String
   */
  public String getDateCreated() {
    return dateCreated;
  }

  /**
   * Getter for longitudeAsNumber, longitude as a number, as it appears in the coordinates of GeometryData class
   *
   * @return the longitude as a Double
   */
  public Double getLongitudeAsNumber() {
    return longitudeAsNumber;
  }

  /**
   * Getter for latitudeAsNumber, latitude as a number, as it appears in the coordinates of GeometryData class
   *
   * @return the latitude as a Double
   */
  public Double getLatitudeAsNumber() {
    return latitudeAsNumber;
  }

  /**
   * Getter for stateAbbreviation, the abbreviation of the state this feature
   * is in. Ex: "NM" for New Mexico.
   *
   * @return the state abbreviation as a String
   */
  public String getStateAbbreviation() {
    return stateAbbreviation;
  }

  /**
   * Getter for elevation, the elevation in meters of the feature
   *
   * @return the elevation in meters as a Double
   */
  public Double getElevation() {
    return elevation;
  }

  /**
   * DGetter for feature classifier, or the type of feature (i.e. Populated Place, Post Office, etc.)
   *
   * @return the type of feature as a String
   */
  public String getFeatureClassifier() {
    return featureClassifier;
  }

  /**
   * Getter for featureName, or the name of the feature
   *
   * @return the name of the feature as a String
   */
  public String getFeatureName() {
    return featureName;
  }

  /**
   * Getter for featureId - self-explanatory
   *
   * @return the id of the feature as a long int
   */
  public long getFeatureId() {
    return featureId;
  }

  /**
   * Getter for the countyName, or the name of the county the feature is in
   *
   * @return the name of the county as a String
   */
  public String getCountyName() {
    return countyName;
  }
}
