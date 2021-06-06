package rad.heydari.mohammad.midterm.project.mafia.commandThings;

import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

public class Demand{
    private Command command;
    private ServerSidePlayerDetails demander;

    public Demand(Command command , ServerSidePlayerDetails demander){
        this.command = command ;
        this.demander = demander;
    }

    public Command getCommand() {
        return command;
    }

    public ServerSidePlayerDetails getDemander() {
        return demander;
    }
}
