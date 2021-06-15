package rad.heydari.mohammad.midterm.project.mafia.roleThings.badGuys;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayerAction;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayersActionTypes;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.BadGuys;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * class for the role , mafia member
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public class Mafia extends Actionable implements BadGuys {

    /**
     * constructor for the role
     * @param objectInputStream the InputStream to communicate with server
     * @param objectOutputStream the outputStream to communicate with server
     * @param userName is the username of the player
     * @param inputProducer is the inputProducer of the player
     */
    public Mafia(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName , InputProducer inputProducer) {
        super(objectInputStream, objectOutputStream , "mafia" , userName , inputProducer);

    }

    /**
     * the action of the player
     * @param command the command that contains the needed things for the action
     */
    @Override
    public void action(Command command) {
        startNow();

        boolean correctlyDone = false;
        ArrayList<String> othersNames = (ArrayList<String>) command.getCommandNeededThings();

        othersNames.remove(getUserName());

        int input = 0;
        Command actionCommand = null;

        while (! correctlyDone){

            if(isTimeOver(getTimeLimit())){
                break;
            }

            System.out.println("choose who to die tonight (as a mafia) : ");
            printStringArrayList(othersNames);

            while (! isTimeOver(getTimeLimit())){
                if(getInputProducer().hasNext()){
                    try {
                        input = Integer.parseInt(getInputProducer().consumeInput());
                        break;
                    }catch (NumberFormatException e){
                        System.out.println("your input is not a number , please try again .");
                    }
                }
                else {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(isTimeOver(getTimeLimit())){
                break;
            }

            if(input == 0){
                System.out.println("you have chosen no one to kill tonight");
                actionCommand = new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.mafiaVictim ,
                        null  , getUserName()));
                correctlyDone = true;
            }

            else if(input > 0 && input <= othersNames.size()){
                System.out.println(" you choose to kill " + othersNames.get(input - 1));
                actionCommand = new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.mafiaVictim ,
                                othersNames.get(input - 1 ) , getUserName()));
                correctlyDone = true;
            }
            else {
                System.out.println("not valid input\nplease try again : ");
            }
        }

        if(!correctlyDone){
            System.out.println("time out , you didn't choose anyone .");
            actionCommand = new Command(CommandTypes.iDoMyAction ,
                    new PlayerAction(PlayersActionTypes.mafiaVictim ,
                    null));
        }

        try {
            getObjectOutputStream().writeObject(actionCommand);
        } catch (IOException e) {
            System.err.println("! you are disconnected from server !");
            System.exit(0);
        }
    }
}
