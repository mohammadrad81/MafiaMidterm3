package rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.God;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.io.IOException;
import java.net.SocketException;

public class RunnableWaitToGetReady implements Runnable{

    private ServerSidePlayerDetails player;

    public RunnableWaitToGetReady(ServerSidePlayerDetails player){
        this.player = player;
    }

    @Override
    public void run(){

        boolean gotReady = false;

        try {
            player.sendCommandToPlayer(new Command(CommandTypes.waitingForClientToGetReady , null));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (! gotReady){
            try {
                Command command = player.receivePlayerRespond();

                if(command.getType() == CommandTypes.imReady){
                    gotReady = true;
                }

                else {
                    God.doTheCommand(command);
                }

            } catch (IOException e) {
                God.removeOfflinePlayerNotifyOthers(player);
                break;
            }
        }
    }
}
