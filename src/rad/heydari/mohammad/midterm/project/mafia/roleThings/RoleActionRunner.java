package rad.heydari.mohammad.midterm.project.mafia.roleThings;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;

public class RoleActionRunner implements Runnable{
    private Actionable actionable;
    private Command command;
    public RoleActionRunner(Actionable actionable , Command command){
        this.actionable = actionable;
        this.command = command;
    }

    @Override
    public void run() {
        actionable.action(command);
    }
}
