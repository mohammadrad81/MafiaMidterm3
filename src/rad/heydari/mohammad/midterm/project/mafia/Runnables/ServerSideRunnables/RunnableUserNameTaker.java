package rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables;


import rad.heydari.mohammad.midterm.project.mafia.MafiaGameException.RepetitiousUserNameException;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerMafiaGameLogic;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;


public class RunnableUserNameTaker implements Runnable{
    private ServerSidePlayerDetails thisPlayerDetails;
    private ServerMafiaGameLogic serverMafiaGameLogic;

    public RunnableUserNameTaker(ServerMafiaGameLogic serverMafiaGameLogic, ServerSidePlayerDetails thisPlayerDetails){
        this.serverMafiaGameLogic = serverMafiaGameLogic;
        this.thisPlayerDetails = thisPlayerDetails;
    }

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
                e.printStackTrace();
                break;
            }catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            } catch (RepetitiousUserNameException e) {

                try {

                    objectOutputStream.writeObject(new Command(CommandTypes.repetitiousUserName ,
                            "this username is already taken"));

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    break;
                }
            }
        }
    }


}
