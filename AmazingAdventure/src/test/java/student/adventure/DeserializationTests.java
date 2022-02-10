package student.adventure;

import maplayout.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DeserializationTests {
    static final String pathname = "src/main/resources/uiuc_squirrel_game.json";

    Gson gson;
    Reader reader;
    MapLayout mapLayout;

    @Before
    public void setUp() {
        // This is run before every test.
        gson = new Gson();

        try {
            reader = Files.newBufferedReader(Paths.get(pathname));
        } catch (Exception e) {
            System.out.println("Unable to open JSON file in path " + pathname);
            return;
        }

        mapLayout = gson.fromJson(reader, MapLayout.class);
    }

    @Test
    // Checks that the Direction class has filled values
    public void testDirectionClassDeserialization() {
        MapArea mapArea = mapLayout.findMapArea(0);
        int nextArea = mapArea.findDirection("NORTH");
        assertNotEquals(nextArea, -1);

        mapArea = mapLayout.findMapArea(14);
        nextArea = mapArea.findDirection("EAST");
        assertNotEquals(nextArea, -1);
        assertEquals(8, nextArea);
    }

    @Test
    // Checks that the MapArea class has filled values
    public void testMapAreaClassDeserialization() {
        MapArea mapArea = mapLayout.findMapArea(0);

        assertEquals(3, mapArea.getInitialThreatLevel());
        assertEquals(0, mapArea.getItemInArea());
        assertEquals(0, mapArea.getAreaId());

        mapArea = mapLayout.findMapArea(15);

        assertEquals(0, mapArea.getInitialThreatLevel());
        assertEquals(0, mapArea.getItemInArea());
        assertEquals(15, mapArea.getAreaId());
    }

    @Test
    // Checks that the Item class has filled values with correct item descriptions
    public void testItemClassDeserialization() {
        List<Item> items = mapLayout.getItemTypes();
        assertNotEquals(items.size(), 0);

        for (Item item : items) {
            int itemId = item.getItemId();

            switch ( itemId ) {
                case 0:
                    assertEquals(item.getItemDescription(), "No item here.");
                    break;
                case 1:
                    assertEquals(item.getItemDescription(), "Baseball Bat - Reduces the threat level in an area to " +
                        "zero. Item breaks after use.");
                    break;
                case 2:
                    assertEquals(item.getItemDescription(), "MedKit - Reduces your injury level to zero.");
                    break;
                case 3:
                    assertEquals(item.getItemDescription(), "Bus Key - A peculiarly shaped key that seems to be for starting a bus.");
                    break;
                default:
                    assertEquals(0, 1);
            }
        }
    }

    @Test
    // Checks that the MapLayout class has filled members after JSON deserialization
    public void testMapLayoutClassDeserialization() {
        assertEquals(mapLayout.getStartAreaId(), 0);
        assertEquals(mapLayout.getEndAreaId(), 15);
        assertNotEquals(mapLayout.getAreas().size(), 0);
        assertNotEquals(mapLayout.getItemTypes().size(), 0);
    }
}