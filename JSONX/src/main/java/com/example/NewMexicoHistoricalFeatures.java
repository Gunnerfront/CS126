package com.example;

import java.util.ArrayList;
import java.util.List;

public class NewMexicoHistoricalFeatures {
  // List of HistoricalFeature Objects that represent historical features in New Mexico
  private List<HistoricalFeature> features;

  // Subtraction metric used for comparing doubles
  private static final Double EPSILON = 0.000001d;

  /**
   * Filtering method for getting a list of only the historical features with known coordinates
   *
   * @return a List container holding HistoricalFeature objects which have known coordinates
   */
  public List<HistoricalFeature> getLocatableFeatures() {
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
   *
   */

  /**
   * Determines whether or not a Double value is roughly equal to zero
   *
   * @param number, a Double for the value you want to check
   * @return true if number is roughly equal to zero, false if not
   */
  private Boolean isEqualToZero(Double number) {
    return Math.abs(number - 0.0) < EPSILON;
  }

}
