package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.InputThings.LoopedTillRightInput;
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

public class TownDoctor extends Actionable implements GoodGuys {

    private boolean hasSavedHisSelf;
//    private LoopedTillRightInput loopedTillRightInput;

    public TownDoctor(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName , InputProducer inputProducer) {
        super(objectInputStream, objectOutputStream , "town doctor" , userName , inputProducer);
//        loopedTillRightInput = new LoopedTillRightInput();

    }

    @Override
    public void action(Command command) {
        startNow();
        Command saveCommand = null;
        boolean correctlyDone = false;
        ArrayList<String> savablePlayersNames = (ArrayList<String>) command.getCommandNeededThings();

        if(hasSavedHisSelf){
            savablePlayersNames.remove(getUserName());
        }

        int input = 0;
        while (!correctlyDone) {

            if(isTimeOver(getTimeLimit())){
                break;
            }

            System.out.println("who do you want to save ? ");
            printStringArrayList(savablePlayersNames);

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

            if (input == 0) {
                System.out.println("you save nobody tonight .");
                saveCommand = new Command(CommandTypes.iDoMyAction,
                        new PlayerAction(PlayersActionTypes.townDoctorSave, null));
                correctlyDone = true;
            }

            else if(input >0 && input <= savablePlayersNames.size()){
                if(savablePlayersNames.get(input -1).equals(getUserName())){
                    hasSavedHisSelf = true;
                    System.out.println("you save yourself tonight .");
                    saveCommand = new Command(CommandTypes.iDoMyAction , new PlayerAction(PlayersActionTypes.townDoctorSave , getUserName()));
                    correctlyDone = true;
                }
                else{
                    System.out.println("you save player : " +
                            savablePlayersNames.get(input -1) +
                            " tonight ." );

                    saveCommand = new Command(CommandTypes.iDoMyAction ,
                            new PlayerAction(PlayersActionTypes.townDoctorSave ,
                            savablePlayersNames.get(input - 1)));
                    correctlyDone = true;
                }
            }
            else {
                System.out.println("wrong input , please try again");
                correctlyDone = false;
            }
        }

        if (! correctlyDone){
            System.out.println("time out , and you didn't choose , so you save nobody tonight .");
            saveCommand = new Command(CommandTypes.iDoMyAction ,
                    new PlayerAction(PlayersActionTypes.townDoctorSave ,
                    null));
        }

        try {
            getObjectOutputStream().writeObject(saveCommand);
        } catch (IOException e) {
            System.err.println("! you are disconnected from server !");
            System.exit(0);
        }

    }
}
