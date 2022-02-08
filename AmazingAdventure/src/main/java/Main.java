import com.google.gson.Gson;
import maplayout.*;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        // Wishing you good luck on your Adventure!
        Gson gson = new Gson();
        final String pathname = "src/main/resources/uiuc_squirrel_game.json";
        Reader reader;

        try {
            reader = Files.newBufferedReader(Paths.get(pathname));
        } catch (Exception e) {
            System.out.println("Unable to open JSON file in path " + pathname);
            return;
        }

        MapLayout mapLayout = gson.fromJson(reader, MapLayout.class);
        AdventureGame game = new AdventureGame(mapLayout);
        game.playGame();
    }
}
