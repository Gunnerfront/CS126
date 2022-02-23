import com.google.gson.Gson;
import maplayout.*;
import org.glassfish.grizzly.http.server.HttpServer;
import student.adventure.ConsoleAdventureGame;
import student.server.AdventureResource;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static student.server.AdventureServer.createServer;

public class Main {

    static final boolean isTerminalSession = false;

    public static void main(String[] args) {
        // Wishing you good luck on your Adventure!
        if (isTerminalSession) {
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
            ConsoleAdventureGame game = new ConsoleAdventureGame(mapLayout);
            game.playGame();
        } else {
            try {
                HttpServer httpServer = createServer(AdventureResource.class);
                httpServer.start();
            } catch ( Exception e ) {
                System.out.println("Failed to start server!\n" + e);
            }
        }
    }
}
