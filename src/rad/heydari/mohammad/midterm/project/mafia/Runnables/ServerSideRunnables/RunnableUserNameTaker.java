package rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables;


import rad.heydari.mohammad.midterm.project.mafia.MafiaGameException.RepetitiousUserNameException;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.God;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerMafiaGameLogic;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

/**
 * runnable class for setting the userName of the player
 * server side
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public class RunnableUserNameTaker implements Runnable{
    private ServerSidePlayerDetails thisPlayerDetails;
    private ServerMafiaGameLogic serverMafiaGameLogic;

    /**
     * simple constructor
     * @param serverMafiaGameLogic is the game logic , the username taker is working for
     * @param thisPlayerDetails is the player , the username taker is about to set his username
     */
    public RunnableUserNameTaker(ServerMafiaGameLogic serverMafiaGameLogic, ServerSidePlayerDetails thisPlayerDetails){
        this.serverMafiaGameLogic = serverMafiaGameLogic;
        this.thisPlayerDetails = thisPlayerDetails;
    }

    /**
     * run method for taking username from the player and setting it
     */
    @Override
    public void run() {

        String inputUserName = null;
        Command clientRespond = null;
        ObjectOutputStream objectOutputStream = thisPlayerDetails.getObjectOutputStream();
        ObjectInputStream objectInputStream = thisPlayerDetails.getObjectInputStream();
        boolean userNameIsSet = false;

        while (! userNameIsSet){

            try {
                clientRespond = (Command) objectInputStream.readObject();

                if(clientRespond.getType() == CommandTypes.setMyUserName){
                     inputUserName = (String) clientRespond.getCommandNeededThings();
                     serverMafiaGameLogic.setUserName(thisPlayerDetails , inputUserName);
                     objectOutputStream.writeObject(new Command(CommandTypes.yourUserNameIsSet ,inputUserName));

                     userNameIsSet = true;
                     break;

                }


                else{
                    //player left the game :
                   serverMafiaGameLogic.doTheCommand(new Command(CommandTypes.iExitTheGame , thisPlayerDetails));
                    break;
                }

            }catch (SocketException e){
                God.removeOfflinePlayerNotifyOthers(thisPlayerDetails);
                break;
            }catch (IOException e) {
                God.removeOfflinePlayerNotifyOthers(thisPlayerDetails);
                break;
            } catch (ClassNotFoundException e) {
                System.out.println("wrong sent from player : " + thisPlayerDetails.getUserName());
                God.removeOfflinePlayerNotifyOthers(thisPlayerDetails);
                break;
            } catch (RepetitiousUserNameException e) {

                try {

                    objectOutputStream.writeObject(new Command(CommandTypes.repetitiousUserName ,
                            "this username is already taken"));

                } catch (IOException ioException) {
                    God.removeOfflinePlayerNotifyOthers(thisPlayerDetails);
                    break;
                }
            }
        }
    }


}
