package com.example;


import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

// Using JSON data from the following website:
// https://catalog.data.gov/dataset/gnis-historic-features
public class JSONXTest {
    private Gson gson;

    @Before
    public void setUp() {
        // This is run before every test, so it will reset the Gson parser every test
        gson = new Gson();
    }

    @Test
    public void sanityCheck() {

        assertEquals(1,1);
    }
}
