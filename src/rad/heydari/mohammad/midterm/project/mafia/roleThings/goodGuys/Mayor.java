package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

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
import java.util.Scanner;
/**
 * class for the role , Mayor
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class Mayor extends Actionable implements GoodGuys {
    /**
     * constructor for the role
     * @param objectInputStream the InputStream to communicate with server
     * @param objectOutputStream the outputStream to communicate with server
     * @param userName is the username of the player
     * @param inputProducer is the inputProducer of the player
     */
    public Mayor(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName , InputProducer inputProducer) {
        super(objectInputStream, objectOutputStream , "mayor" , userName , inputProducer);

    }
    /**
     * the action of the player
     * @param command the command that contains the needed things for the action
     */
    @Override
    public void action(Command command) {
        startNow();
        boolean correctlyDone = false;
        char input = 'n';
        Command actionCommand = null;
        while (! correctlyDone){

            if(isTimeOver(getTimeLimit())){
                break;
            }

            System.out.println("do you want to cancel the voting ? (y/n): ");

            while (! isTimeOver(getTimeLimit())){
                if(getInputProducer().hasNext()){
                    input = getInputProducer().consumeInput().charAt(0);
                    break;
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


            if(input == 'n'){
                System.out.println("ok , you don't cancel the voting .");

                actionCommand = new Command(CommandTypes.mayorSaysLynch , null);
                correctlyDone = true;
            }
            else if(input == 'y'){
                System.out.println("ok , you , the mayor cancel the voting .");
                actionCommand = new Command(CommandTypes.mayorSaysDontLynch , null);
                correctlyDone = true;
            }
            else {
                System.out.println("not valid input\nplease try again :");
            }
        }

        if(! correctlyDone){
            System.out.println("time out , you didn't choose , so the lynch will be done .");
            actionCommand = new Command(CommandTypes.mayorSaysLynch , null);
        }
        try {
            getObjectOutputStream().writeObject(actionCommand);
        } catch (IOException e) {
            System.err.println("! you are disconnected from server !");
            System.exit(0);
        }

    }
}
