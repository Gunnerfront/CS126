package com.example;

//import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.List;

public class NewMexicoHistoricalFeatures {
  // List of HistoricalFeature Objects that represent historical features in New Mexico
  private List<HistoricalFeature> features;

  // Subtraction metric used for comparing doubles
  private static final Double EPSILON = 0.000001d;

  // Constants representing miles per degree longitude or latitude
  private static final Double MILES_PER_DEGREE_LONGITUDE = 54.6;
  private static final Double MILES_PER_DEGREE_LATITUDE = 69.0;


  public List<HistoricalFeature> getFeatures () {
    return features;
  }
  /**
   * Filtering method for getting a list of only the historical features with known coordinates
   *
   * @return a List container holding HistoricalFeature objects which have known coordinates
   */
  public List<HistoricalFeature> findLocatableFeatures() {
    List<HistoricalFeature> locatableFeatures = new ArrayList<HistoricalFeature>();
    for (HistoricalFeature feature : features) {
      Double longitude = feature.getPropertyData().getLongitudeAsNumber();
      Double latitude = feature.getPropertyData().getLatitudeAsNumber();

      if (isEqualToZero(longitude) && isEqualToZero(latitude)) {
        locatableFeatures.add(feature);
      }
    }

    return locatableFeatures;
  }

  /**
   * Filtering method for getting all historical features in a particular county.
   * Since this class focuses on New Mexico features, the input parameter is limited.
   *
   * @param county, the particular county whose features you want to get; not case-sensitive
   * @return a List container holding HistoricalFeature objects which are located in
   *         the inputted county; returns an empty List if there are no features in this county
   */
  public List<HistoricalFeature> findFeaturesInCounty(String county) {
    List<HistoricalFeature> featuresInCounty = new ArrayList<HistoricalFeature>();
    for (HistoricalFeature feature : features) {
      String featuresCounty = feature.getPropertyData().getCountyName();

      if (county.equalsIgnoreCase(featuresCounty)) {
        featuresInCounty.add(feature);
      }
    }

    return featuresInCounty;
  }

  /**
   * Filtering method for getting all the Historical features which are at a certain distance
   * from a particular latitude-longitude coordinate
   *
   * @param latitude degrees as a Double, part of the coordinate from which to search
   * @param longitude degrees as a Double, part of the coordinate from which to search
   * @param desiredDistance Double distance in miles from the coordinate to search for features from
   * @return A List of HistoricalFeature objects that are within the distance
   *         of the latitude-longitude coordinate provided
   */
  public List<HistoricalFeature> findNearbyFeatures(Double latitude, Double longitude, Double desiredDistance) {
    List<HistoricalFeature> nearbyFeatures = findLocatableFeatures();
    for (HistoricalFeature feature : features) {
      Double milesLongitudallyAway =
          Math.abs(feature.getPropertyData().getLongitudeAsNumber()) * MILES_PER_DEGREE_LONGITUDE;
      Double milesLatitudallyAway =
          Math.abs(feature.getPropertyData().getLatitudeAsNumber()) * MILES_PER_DEGREE_LATITUDE;
      Double actualDistance =
          Math.sqrt(milesLongitudallyAway * milesLongitudallyAway - milesLatitudallyAway * milesLatitudallyAway);

      if (actualDistance < desiredDistance) {
        nearbyFeatures.add(feature);
      }
    }

    return nearbyFeatures;
  }

  /**
   * Filtering method for finding the historical features that were added to the dataset
   * between the two specified years.
   *
   * @param earliest, earliest bound of year to search for, inclusive
   * @param latest, latest bound of year to search for, exclusive
   * @return
   */
  public List<HistoricalFeature> findFeaturesAddedInYearRange(int earliest, int latest) {
    List<HistoricalFeature> featuresAddedInRange = new ArrayList<HistoricalFeature>();
    for (HistoricalFeature feature : features) {
      String featuresAddDate = feature.getPropertyData().getDateCreated();

      // Checks if date in dataset is valid
      if (isValidDateFormat(featuresAddDate)) {
        int year = Integer.parseInt(featuresAddDate.substring(0, 4));

        if (year < latest && year >= earliest) {
          featuresAddedInRange.add(feature);
        }
      }
    }

    return featuresAddedInRange;
  }

  /**
   * Helper function to determine if a string is in the date format YYYY/MM/DD
   *
   * @param date, String you want to determine if it has the proper YYYY/MM/DD formatting
   * @return True if date is valid and in that format, false if not
   */
  private Boolean isValidDateFormat(String date) {
    if (date.length() != 10) {
      return false;
    }

    try {
      int year = Integer.parseInt(date.substring(0, 4));
      int month = Integer.parseInt(date.substring(5, 7));
      int day = Integer.parseInt(date.substring(8));

      if (month < 1 || month > 12 || day > 31 || day < 1 || year < 0) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  /**
   * Helper function that determines whether a Double value is roughly equal to zero
   *
   * @param number, a Double for the value you want to check
   * @return True if number is roughly equal to zero, False if not
   */
  private Boolean isEqualToZero(Double number) {

    return Math.abs(number - 0.0) < EPSILON;
  }

}
