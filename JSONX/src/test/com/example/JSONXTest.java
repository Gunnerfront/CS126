package com.example;


import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

// Using JSON data from the following website:
// https://catalog.data.gov/dataset/gnis-historic-features
public class JSONXTest {
    private Gson gson;

    private static final String myJson = "{" +
        "\"features\": [{" +
        "    \"geometry\": {" +
        "      \"type\": \"Point\"," +
        "      \"coordinates\": [-107.3175179999999, 32.22536869999996]" +
        "       }," +
        "    \"type\": \"Feature\"," +
        "    \"properties\": {" +
        "      \"DATE_CREAT\": \"1993/08/01\"," +
        "      \"PRIM_LONG1\": -107.317518," +
        "      \"STATE_ALPH\": \"NM\"," +
        "      \"ELEVATION\": 1286.0," +
        "      \"PRIMARY_LA\": \"321331N\"," +
        "      \"FEATURE_CL\": \"Post Office\"," +
        "      \"observed\": \"\"," +
        "      \"SOURCE_LAT\": \"\"," +
        "      \"FEATURE_NA\": \"Cambray Post Office (historical)\"," +
        "      \"PRIM_LONG_\": \"1071903W\"," +
        "      \"id\": 8361479," +
        "      \"FEATURE_ID\": 937222.0," +
        "      \"SOURCE_L_2\": \"\"," +
        "      \"STATE_NUME\": 35.0," +
        "      \"MAP_NAME\": \"Cambray\"," +
        "      \"COUNTY_NAM\": \"Luna\"," +
        "      \"DATE_EDITE\": \"\"," +
        "      \"SOURCE_LON\": \"\"," +
        "      \"COUNTY_NUM\": 29.0," +
        "      \"SOURCE_L_1\": \"\"," +
        "      \"PRIM_LAT_D\": 32.225369" +
        "       }" +
        "   ]" +
        "  }";

    @Before
    public void setUp() {
        // reset the Gson parser before every test
        gson = new Gson();
    }

    @Test
    public void test_JSONStringFormat() {
        Boolean correctJSON = true;
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(myJson, NewMexicoHistoricalFeatures.class);
        } catch ( Exception e ) {
            correctJSON = false;
        }
        assertEquals(true, correctJSON);
    }

    @Test
    public void test_findFeaturesInCountyLunaCounty() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(myJson, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> lunaFeatures = newMexicoHistoricalFeatures.findFeaturesInCounty("Luna");

            assertEquals(lunaFeatures.size(), 57);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }

    @Test
    public void test_findFeaturesInCountyRioArribaCounty() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(myJson, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> rioArribaFeatures = newMexicoHistoricalFeatures.findFeaturesInCounty("Rio Arriba");

            assertEquals(rioArribaFeatures.size(), 100);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }

    @Test
    public void test_findLocatableFeatures() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(myJson, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> locatableFeatures = newMexicoHistoricalFeatures.findLocatableFeatures();

            assertEquals(locatableFeatures.size(), 795);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }

    @Test
    public void test_findFeaturesAddedInYearRange() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(myJson, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> featuresInRange =
                newMexicoHistoricalFeatures.findFeaturesAddedInYearRange(1980, 1981);

            assertEquals(featuresInRange.size() == 53, true);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }

    @Test
    public void test_findNearbyFeaturesClose() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(myJson, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> nearbyFeatures =
                newMexicoHistoricalFeatures.findNearbyFeatures(-106.0, 35.0, 5.0);

            assertEquals(nearbyFeatures.size(), 1);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }

    @Test
    public void test_findNearbyFeaturesAllKnownLocations() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(myJson, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> nearbyFeatures =
                newMexicoHistoricalFeatures.findNearbyFeatures(-106.0, 35.0, 1000.0);

            assertEquals(nearbyFeatures.size() == 795, true);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }
}
