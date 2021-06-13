package rad.heydari.mohammad.midterm.project.mafia.gameThings;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
/** an interface of the game on the client side
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @see Command
 */
public interface ClientSideGame {
    public void doTheCommand(Command command);
}
