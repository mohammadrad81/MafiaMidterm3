package rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;

/**
 * runnable for player to do the action of his role
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public class RunnableActionDoer implements Runnable{
    private Actionable actionable;
    private Command command;

    /**
     * simple constructor for runnable
     * @param actionable is an actionable role
     * @param command a command that contains the needed things for the player to do his action
     */
    public RunnableActionDoer(Actionable actionable , Command command){
        this.actionable = actionable;
        this.command = command;
    }

    /**
     * the run method to start doing the action
     */
    @Override
    public void run() {
        actionable.action(command);
    }
}
