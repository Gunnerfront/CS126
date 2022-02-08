package student.server;

/**
 * Holds information about a command issued by a client. Includes the command's name
 * and arguments.
 */
public class Command {
    /**
     * The string representing the command's action (e.g.: "go", "take", "attend").
     */
    private String commandName;
    /**
     * The argument to the command (e.g.: "North", "sweatshirt", "CS 126 Lecture").
     */
    private String commandValue;
    /**
     * The name of the player who issued this command. Included so you can track them in the leaderboard
     * if this command would win them the game.
     */
    private String playerName;

    public Command() {

    }

    public Command(String commandName, String commandValue) {
        this.commandName = commandName;
        this.commandValue = commandValue;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getCommandValue() {
        return commandValue;
    }

    public String getPlayerName() {
        return playerName;
    }
}
