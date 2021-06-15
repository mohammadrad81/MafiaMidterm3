package rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.God;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.io.IOException;
import java.net.SocketException;

/**
 * runnable class to wait for the player to get ready to play
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public class RunnableWaitToGetReady implements Runnable{

    private ServerSidePlayerDetails player;

    /**
     * simple constructor
     * @param player is the player , the server is waiting to get ready
     */
    public RunnableWaitToGetReady(ServerSidePlayerDetails player){
        this.player = player;
    }

    /**
     * run method for waiting for player to get ready
     */
    @Override
    public void run(){

        boolean gotReady = false;

        try {
            player.sendCommandToPlayer(new Command(CommandTypes.waitingForClientToGetReady , null));
        } catch (IOException e) {
            God.removeOfflinePlayerNotifyOthers(player);
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
