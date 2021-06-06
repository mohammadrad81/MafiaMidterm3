package rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Demand;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.God;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.io.IOException;
import java.io.ObjectInputStream;

public class RunnableNightPlayerHandler implements Runnable{

    private ServerSidePlayerDetails player;


    public RunnableNightPlayerHandler(ServerSidePlayerDetails player){
        this.player = player;

    }
    @Override
    public void run() {
        Command command = null;
        Demand demand = null;
        while (true){
            try {
                command = player.receivePlayerRespond();
                God.doTheCommand(command);

                if(command.getType() == CommandTypes.iDoMyAction){
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
