package student.server;

import java.util.List;
import java.util.Map;

/**
 * An object representing the current state of a game instance.
 */
public class GameStatus {
    /**
     * Whether or not this response is an error state.
     * Note: this should not be `true` for most cases. This should only be `true` if an exception was
     * thrown by the game engine.
     */
    private boolean error;
    /**
     * The instance ID associated with this GameStatus.
     * This field is required, and cannot be null.
     */
    private int id;
    /**
     * A text message to display to the user.
     * This field is required, and cannot be null.
     */
    private String message;
    /**
     * A URL of an image to display to the user.
     */
    private String imageUrl;
    /**
     * A YouTube video link to play for the user.
     */
    private String videoUrl;
    /**
     * An object (that you may modify) that contains values represented by the game's state.
     * E.g.: life total, # items in inventory, etc.
     * This field is required, and cannot be null. However, the AdventureState class can be empty if you don't need it.
     */
    private AdventureState state;
    /**
     * An mapping of commands to possible arguments for those commands. This will be used to create buttons on the
     * frontend, so it should be full of the possible options for the user.
     * E.g.: "go" -> ["up", "north", "down"]
     *       "examine" -> [] (need an empty list for no arguments)
     *       "answer" -> ["A", "B", "C", "D"] (for a trivia-like custom feature)
     * This field is required, and cannot be null.
     */
    private Map<String, List<String>> commandOptions;

    public GameStatus(boolean error, int id, String message, String imageUrl, String videoUrl, AdventureState state, Map<String, List<String>> commandOptions) {
        this.error = error;
        this.id = id;
        this.message = message;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.state = state;
        this.commandOptions = commandOptions;
    }

    public boolean isError() {
        return error;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public AdventureState getState() {
        return state;
    }

    public Map<String, List<String>> getCommandOptions() {
        return commandOptions;
    }
}
