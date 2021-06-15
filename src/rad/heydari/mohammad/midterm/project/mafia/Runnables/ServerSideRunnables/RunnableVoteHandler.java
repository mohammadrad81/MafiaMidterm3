package rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.God;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.io.IOException;
/**
 * runnable class for handling the vote from the player
 * server side
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public class RunnableVoteHandler implements Runnable{

    private ServerSidePlayerDetails voter;

    public RunnableVoteHandler(ServerSidePlayerDetails voter){
        this.voter = voter;
    }

    @Override
    public void run() {
        boolean hasVoted = false;

        while (! hasVoted){
            Command command = null;
            try {
                command = voter.receivePlayerRespond();
                if(command.getType() == CommandTypes.iVote){
                    hasVoted = true;
                    God.doTheCommand(command);
                    break;
                }

                else{
                    God.doTheCommand(command);
                }
            } catch (IOException e) {
                God.removeOfflinePlayerNotifyOthers(voter);
                break;
            }
        }
    }

}
