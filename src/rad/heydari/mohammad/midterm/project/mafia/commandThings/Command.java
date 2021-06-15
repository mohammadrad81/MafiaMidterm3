package rad.heydari.mohammad.midterm.project.mafia.commandThings;

import java.io.Serializable;
/** is a class for communication between server and client
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public class Command implements Serializable {
    private CommandTypes title;
    private Serializable CommandNeededThings;

    /**
     * the constructor of the command
     * @param title is the title of the command ( the type of the command )
     * @param commandNeededThings is the object the command needs
     */
    public Command(CommandTypes title, Serializable commandNeededThings) {
        this.title = title;
        CommandNeededThings = commandNeededThings;
    }

    /**
     * getter method for the type of the command
     * @return the type of the command
     */
    public CommandTypes getType() {
        return title;
    }

    /**
     * getter method for the command needed things
     * @return the command needed thing
     */
    public Serializable getCommandNeededThings() {
        return CommandNeededThings;
    }

}
