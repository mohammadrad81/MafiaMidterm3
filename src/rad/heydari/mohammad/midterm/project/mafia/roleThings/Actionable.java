package rad.heydari.mohammad.midterm.project.mafia.roleThings;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
/**
 * class for the role , Actionable
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public abstract class Actionable implements Role {
    private String userName;
    private String roleNameString;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private InputProducer inputProducer;
    private long startSecond;
    private long timeLimit = 210;

    /**
     * constructor for the Actionable
     * @param objectInputStream the inputStream to communicate with server
     * @param objectOutputStream the outputStream to communicate with server
     * @param roleNameString the
     * @param userName
     * @param inputProducer
     */
    public Actionable(ObjectInputStream objectInputStream ,
                      ObjectOutputStream objectOutputStream ,
                      String roleNameString,
                      String userName,
                    InputProducer inputProducer){
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.roleNameString = roleNameString;
        this.userName = userName;
        this.inputProducer = inputProducer;
    }
    /**
     * the action of the player
     * @param command the command that contains the needed things for the action
     */
    public abstract void action(Command command);

    /**
     *
     * @return the objectOutputStream
     */
    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    /**
     *
     * @return the objectInputStream
     */
    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    /**
     *
     * @return the userName of the player
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @return the name of the role
     */
    @Override
    public String getRoleString() {
        return roleNameString;
    }

    /**
     * prints an arrayList , with 0 as the choice no one
     * @param names is the arrayList of the players needed for the action of the player
     */
    public void printStringArrayList(ArrayList<String> names){
        System.out.println("0- no one");
        for(int i = 1; i <= names.size(); i++){
            System.out.println(i + "- " +names.get(i-1));
        }
    }

    /**
     *
     * @return the inputProducer of the player ( client )
     */
    public InputProducer getInputProducer() {
        return inputProducer;
    }

    /**
     * sets the start of action as seconds from 1970
     */
    public void startNow(){
        startSecond = java.time.Instant.now().getEpochSecond();
    }

    /**
     * checks if the time is over or not
     * @param timeLimit is the limit of doing the action
     * @return true if the time is over , else false
     */
    public boolean isTimeOver(long timeLimit){
        long nowSecond = java.time.Instant.now().getEpochSecond();
        if(nowSecond >= startSecond + timeLimit){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *
     * @return the timeLimit
     */
    public long getTimeLimit() {
        return timeLimit;
    }

}