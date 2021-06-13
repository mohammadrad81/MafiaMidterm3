package rad.heydari.mohammad.midterm.project.mafia.gameThings;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;
/**
 * the interface of the game on the server side
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public interface ServerSideGame {

    public void startTheGame();
    public void doTheCommand(Command command);
    public void removeOfflinePlayerNotifyOthers(ServerSidePlayerDetails removingPlayer);

}
