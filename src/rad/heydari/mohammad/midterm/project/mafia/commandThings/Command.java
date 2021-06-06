package rad.heydari.mohammad.midterm.project.mafia.commandThings;

import java.io.Serializable;

public class Command implements Serializable {
    private CommandTypes title;
    private Serializable CommandNeededThings;

    public Command(CommandTypes title, Serializable commandNeededThings) {
        this.title = title;
        CommandNeededThings = commandNeededThings;
    }

    public CommandTypes getType() {
        return title;
    }

    public void setTitle(CommandTypes title) {
        this.title = title;
    }

    public Serializable getCommandNeededThings() {
        return CommandNeededThings;
    }

    public void setCommandNeededThings(Serializable commandNeededThings) {
        CommandNeededThings = commandNeededThings;
    }

}
