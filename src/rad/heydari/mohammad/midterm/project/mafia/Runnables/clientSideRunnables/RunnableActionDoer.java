package rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;

public class RunnableActionDoer implements Runnable{
    private Actionable actionable;
    private Command command;
    public RunnableActionDoer(Actionable actionable , Command command){
        this.actionable = actionable;
        this.command = command;
    }
    @Override
    public void run() {
        actionable.action(command);
    }
}
