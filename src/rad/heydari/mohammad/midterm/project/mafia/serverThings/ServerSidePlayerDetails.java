package rad.heydari.mohammad.midterm.project.mafia.serverThings;


import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.RoleNames;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
/**
 * class for ServerSidePlayerDetails
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public class ServerSidePlayerDetails implements Serializable {
    private Socket socket;
    private String userName;
    private RoleNames roleName;
    private boolean isMuted;
//    private boolean isAlive;
//    private boolean isSpectator;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    /**
     * constructor
     * @param socket is the socket to communicate with the player
     * @param objectOutputStream is the outputStream to communicate with the player
     * @param objectInputStream is the inputStream to communicate with the player
     */
    public ServerSidePlayerDetails(Socket socket,
                                   ObjectOutputStream objectOutputStream ,
                                   ObjectInputStream objectInputStream){
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

//    public Socket getSocket() {
//        return socket;
//    }

    /**
     *
     * @return the username of the player
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @return the roleName of the player
     */
    public RoleNames getRoleName() {
        return roleName;
    }

    /**
     * setter for the player roleName
     * @param roleName
     */
    public void setRoleName(RoleNames roleName) {
        this.roleName = roleName;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }
//
//    public boolean isAlive() {
//        return isAlive;
//    }
//
//    public void setAlive(boolean alive) {
//        isAlive = alive;
//    }
//
//    public boolean isSpectator() {
//        return isSpectator;
//    }

    /**
     *
     * @return the objectInputStream for communication with player
     */
    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    /**
     *
     * @return the objectOutputStream for communication with player
     */
    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    /**
     * setter for username of the player
     * @param userName is the setting username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

//    public void setSpectator(boolean spectator) {
//        isSpectator = spectator;
//    }

    /**
     * sends a command to player
     * @param command is the command we want to send to player
     * @throws IOException if there is a problem with connection
     */
    public void sendCommandToPlayer(Command command) throws IOException {
            objectOutputStream.writeObject(command);
    }

    /**
     * waits for the respond of the player
     * @return the command from the player as respond
     * @throws IOException if there is a problem with connection
     */
    public Command receivePlayerRespond() throws IOException {
        Command respond = null;
        try {
            respond = (Command) objectInputStream.readObject();
        }  catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return respond;
    }

    /**
     * sends a roleName to the player to take his role
     * @param roleName is the sending role name
     * @throws IOException if there is a problem with connection
     */
    public void sendTheRoleToThePlayer(RoleNames roleName) throws IOException {
        objectOutputStream.writeObject(new Command(CommandTypes.takeYourRole , roleName));
    }
}
