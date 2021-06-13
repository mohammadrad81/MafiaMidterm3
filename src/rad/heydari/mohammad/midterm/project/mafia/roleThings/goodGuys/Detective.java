package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayerAction;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayersActionTypes;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * class for the role , Detective
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class Detective extends Actionable {
    /**
     * constructor for the role
     * @param objectInputStream the InputStream to communicate with server
     * @param objectOutputStream the outputStream to communicate with server
     * @param userName is the username of the player
     * @param inputProducer is the inputProducer of the player
     */
    public Detective(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName , InputProducer inputProducer) {
        super(objectInputStream, objectOutputStream, "detective" , userName , inputProducer);

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

        while (! correctlyDone) {

            if(isTimeOver(getTimeLimit())){
                break;
            }

            System.out.println("choose someOne to know he is evil or not : ");
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
                else{
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
                System.out.println("ok you have chosen no one .");
                actionCommand = new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.detect ,
                        null));
                correctlyDone = true;
            }
            else if(input > 0 && input <= othersNames.size()){
                System.out.println("ok , you have chosen to know " + othersNames.get(input - 1)+ " is evil or not .");
                actionCommand = new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.detect ,
                        othersNames.get(input - 1)));
                correctlyDone = true;

            }
            else {
                System.out.println("not valid input\nplease try again :");
            }
        }

        if(! correctlyDone){
            System.out.println("you didn't choose any one .");
            actionCommand = new Command(CommandTypes.iDoMyAction ,
                    new PlayerAction(PlayersActionTypes.detect ,
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
