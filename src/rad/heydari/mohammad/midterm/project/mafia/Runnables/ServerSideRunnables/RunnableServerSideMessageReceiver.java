package rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.God;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.io.IOException;

public class RunnableServerSideMessageReceiver implements Runnable{
    private ServerSidePlayerDetails player;

    public RunnableServerSideMessageReceiver(ServerSidePlayerDetails player){
        this.player = player;
    }
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
