package rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.God;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.io.IOException;

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
