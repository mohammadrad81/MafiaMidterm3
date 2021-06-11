package rad.heydari.mohammad.midterm.project.mafia.serverThings;


import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.RoleNames;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ServerSidePlayerDetails implements Serializable {
    private Socket socket;
    private String userName;
    private RoleNames roleName;
    private boolean isMuted;
    private boolean isAlive;
    private boolean isSpectator;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public ServerSidePlayerDetails(Socket socket,
                                   ObjectOutputStream objectOutputStream ,
                                   ObjectInputStream objectInputStream){
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUserName() {
        return userName;
    }

    public RoleNames getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleNames roleName) {
        this.roleName = roleName;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
    }

    public void sendCommandToPlayer(Command command) throws IOException {
            objectOutputStream.writeObject(command);
    }
    //why a while loop ? :
    public Command receivePlayerRespond() throws IOException {
        Command respond = null;
        try {
            respond = (Command) objectInputStream.readObject();
        }  catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return respond;
    }

    public void sendTheRoleToThePlayer(RoleNames roleName) throws IOException {
        objectOutputStream.writeObject(new Command(CommandTypes.takeYourRole , roleName));
    }
}
