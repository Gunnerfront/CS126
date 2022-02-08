package com.example;


import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

// Using JSON data from the following website:
// https://catalog.data.gov/dataset/gnis-historic-features
public class JSONXTest {
    private Gson gson;

    private Reader reader;

    @Before
    public void setUp() {
        // reset the Gson parser before every test
        gson = new Gson();
        try {
            reader = Files.newBufferedReader(Paths.get("src/main/java/com/example/NM_Historical_Features.json"));
        } catch (Exception e) {
            System.out.println("Could not open JSON file.");
        }
    }

    @Test
    public void testJSONFileOpening() {
        Boolean correctJSON = true;
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(reader, NewMexicoHistoricalFeatures.class);
        } catch ( Exception e ) {
            correctJSON = false;
        }
        assertEquals(true, correctJSON);
    }

    @Test
    public void testFindFeaturesInCountyLunaCounty() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(reader, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> lunaFeatures = newMexicoHistoricalFeatures.findFeaturesInCounty("Luna");

            assertEquals(lunaFeatures.size(), 57);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }

    @Test
    public void testFindFeaturesInCountyRioArribaCounty() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(reader, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> rioArribaFeatures = newMexicoHistoricalFeatures.findFeaturesInCounty("Rio Arriba");

            assertEquals(rioArribaFeatures.size(), 100);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }

    @Test
    public void testFindLocatableFeatures() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(reader, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> locatableFeatures = newMexicoHistoricalFeatures.findLocatableFeatures();

            assertEquals(locatableFeatures.size(), 795);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }

    @Test
    public void testFindFeaturesAddedInYearRange() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(reader, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> featuresInRange =
                newMexicoHistoricalFeatures.findFeaturesAddedInYearRange(1980, 1981);

            assertEquals(featuresInRange.size() == 53, true);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }

    @Test
    public void testFindNearbyFeaturesClose() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(reader, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> nearbyFeatures =
                newMexicoHistoricalFeatures.findNearbyFeatures(-106.0, 35.0, 5.0);

            assertEquals(nearbyFeatures.size(), 1);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }

    @Test
    public void testFindNearbyFeaturesAllKnownLocations() {
        try {
            NewMexicoHistoricalFeatures newMexicoHistoricalFeatures =
                gson.fromJson(reader, NewMexicoHistoricalFeatures.class);
            List<HistoricalFeature> nearbyFeatures =
                newMexicoHistoricalFeatures.findNearbyFeatures(-106.0, 35.0, 1000.0);

            assertEquals(nearbyFeatures.size() == 795, true);

        } catch ( Exception e ) {
            assertEquals(true, false);
        }
    }
}
