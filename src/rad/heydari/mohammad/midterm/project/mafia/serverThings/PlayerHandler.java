package rad.heydari.mohammad.midterm.project.mafia.serverThings;

import rad.heydari.mohammad.midterm.project.mafia.MafiaGameException.WrongObjectToReadException;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlayerHandler implements Runnable{
    private ServerSidePlayerDetails serverSidePlayerDetails;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public PlayerHandler(ServerSidePlayerDetails serverSidePlayerDetails) throws IOException {
        this.objectInputStream = new ObjectInputStream(this.serverSidePlayerDetails.getSocket().getInputStream());
        this.objectOutputStream = new ObjectOutputStream(this.serverSidePlayerDetails.getSocket().getOutputStream());
    }

    public ServerSidePlayerDetails getServerSidePlayer(){
        return serverSidePlayerDetails;
    }

    public void sendCommandToPlayer(Command command){

        synchronized (objectOutputStream){
            try {
                objectOutputStream.writeObject(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void run() {

        Object readObject = null;

        while (true){
            try {
                readObject =  objectInputStream.readObject();

                if(readObject instanceof  Command){

                }
                else {
                    throw new WrongObjectToReadException("the player sent an invalid Object (not a command)");
                }

            }catch (IOException e) {
                e.printStackTrace();
            }catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (WrongObjectToReadException e) {
                e.printStackTrace();
            }
        }
    }
}
