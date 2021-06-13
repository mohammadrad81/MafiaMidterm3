package rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.God;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.io.IOException;

/**
 * runnable class for handling the night actions of the player
 * server side
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class RunnableNightPlayerHandler implements Runnable{

    private ServerSidePlayerDetails player;

    /**
     * simple constructor
     * @param player is the player should do his action
     */
    public RunnableNightPlayerHandler(ServerSidePlayerDetails player){
        this.player = player;

    }

    /**
     * the run method for receiving the players action command
     */
    @Override
    public void run() {
        Command command = null;
        while (true){
            try {
                command = player.receivePlayerRespond();
                if(command == null){
                    break;
                }
                else if(command.getType() == CommandTypes.iDoMyAction){
                    God.doTheCommand(command);
                    break;
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
