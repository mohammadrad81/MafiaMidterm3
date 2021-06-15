package rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.God;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.io.IOException;
/**
 * runnable class for receiving messages from player
 * server side
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public class RunnableServerSideMessageReceiver implements Runnable{
    private ServerSidePlayerDetails player;

    /**
     * simple constructor
     * @param player is the player , the server receives messages from
     */
    public RunnableServerSideMessageReceiver(ServerSidePlayerDetails player){
        this.player = player;
    }

    /**
     * the run method for receiving messages from the client
     */
    @Override
    public void run() {

        boolean isReady = false;

        Command command = null;
        while (! isReady){

            try {
                command = player.receivePlayerRespond();
                if(command.getType() == CommandTypes.imReady){
                    God.doTheCommand(new Command(CommandTypes.messageToOthers , new Message(player.getUserName() , "i am ready")));
                    isReady = true;
                }

                else if(command.getType() == CommandTypes.messageToOthers){
                    God.doTheCommand(command);
                }
            } catch (IOException e) {
//                e.printStackTrace();
                God.removeOfflinePlayerNotifyOthers(player);
                break;
            }
        }
    }
}
