package rad.heydari.mohammad.midterm.project.mafia.roleThings.badGuys;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayerAction;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayersActionTypes;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.GoodGuys;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * a class for godfather role
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class GodFather extends Actionable implements GoodGuys {
    /**
     * constructor for the role
     * @param objectInputStream the InputStream to communicate with server
     * @param objectOutputStream the OutputStream to communicate with server
     * @param userName is the username of the player
     * @param inputProducer is the inputProducer of the player
     */
    public GodFather(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName , InputProducer inputProducer) {
        super(objectInputStream, objectOutputStream , "godfather" , userName , inputProducer);

    }

    /**
     * the action of the role
     * @param command
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

            System.out.println("choose someone to kill tonight (as godfather of the mafia) :");
            printStringArrayList(othersNames);

            while (! isTimeOver(getTimeLimit())){
                if(getInputProducer().hasNext()){
                    try {
                        input = Integer.parseInt(getInputProducer().consumeInput());
                        break;
                    }catch (NumberFormatException e){
                        System.out.println("the input is not a number , please try again .");
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

            if (input == 0){
                System.out.println("ok , kills nobody tonight .");
                correctlyDone = true;
                actionCommand = new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.godFatherVictim ,
                        null));
            }
            else if(input > 0 && input <= othersNames.size()){
                System.out.println("ok , mafia kills player : " + othersNames.get(input - 1) + " tonight ." );
                correctlyDone = true;
                actionCommand = new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.godFatherVictim ,
                        othersNames.get(input - 1)));
            }
            else {
                System.out.println("not valid input\nplease try again : ");
            }
        }

        if(! correctlyDone){
            System.out.println("time out , you didn't choose anybody , so mafia kills no one tonight .");
            actionCommand = new Command(CommandTypes.iDoMyAction ,
                    new PlayerAction(PlayersActionTypes.godFatherVictim ,
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
