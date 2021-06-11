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

public class DoctorLector extends Actionable implements BadGuys {
    private boolean hasSavedHimSelf ;
//    private Scanner scanner;

    public DoctorLector(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName , InputProducer inputProducer) {
        super(objectInputStream, objectOutputStream , "doctor lector", userName , inputProducer);
//        scanner = new Scanner(System.in);
    }

    @Override
    public void action(Command command) {
        startNow();
        boolean correctlyDone = false;
        ArrayList<String> badGuysNames = (ArrayList<String>) command.getCommandNeededThings();
        if(hasSavedHimSelf){
            badGuysNames.remove(getUserName());
        }
        int input = 0;
        Command actionCommand = null;

        while (! correctlyDone){

            if(isTimeOver(getTimeLimit())){
                break;
            }

            System.out.println("choose someone to save doctor lector : ");
            printStringArrayList(badGuysNames);

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

            if(input == 0){
                System.out.println("ok , you have chosen to don't save any one .");
                actionCommand = new Command(CommandTypes.iDoMyAction,
                        new PlayerAction(PlayersActionTypes.doctorLectorSave,
                                null));
                correctlyDone = true;

            }
            else if(input > 0 && input <= badGuysNames.size()){
                System.out.println("ok , you have chosen to save " + badGuysNames.get(input - 1));
                actionCommand = new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.doctorLectorSave,
                        badGuysNames.get(input - 1)));
                correctlyDone = true;
            }
            else {
                System.out.println("not valid input\nplease try again");
            }
        }

        if(! correctlyDone){
            System.out.println("time out , you didn't choose anybody , so nobody will be saved tonight .");
            actionCommand = new Command(CommandTypes.iDoMyAction ,
                    new PlayerAction(PlayersActionTypes.doctorLectorSave ,
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
