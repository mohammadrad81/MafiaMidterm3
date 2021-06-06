package rad.heydari.mohammad.midterm.project.mafia.gameThings;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

public interface ServerSideGame {

    public void startTheGame();
    public void doTheCommand(Command command);
    public void removeOfflinePlayerNotifyOthers(ServerSidePlayerDetails removingPlayer);

}
