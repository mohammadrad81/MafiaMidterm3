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
import java.util.ArrayList;
import java.util.Scanner;

public class Therapist extends Actionable implements GoodGuys {
//    private Scanner scanner;
    public Therapist(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName , InputProducer inputProducer) {
        super(objectInputStream, objectOutputStream , "therapist", userName , inputProducer);
//        scanner = new Scanner(System.in);
    }

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

            System.out.println("choose somebody to mute tomorrow : ");
            printNameArrayList(othersNames);


            while (! isTimeOver(getTimeLimit())){
                if(getInputProducer().hasNext()){
                    try {
                        input = Integer.parseInt(getInputProducer().consumeInput());
                        break;
                    }catch (NumberFormatException e){
                        System.out.println("input is not a number , please try again .");
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


            if(input == 0){
                System.out.println("you mute nobody today .");
                actionCommand = new Command(CommandTypes.iDoMyAction , new PlayerAction(PlayersActionTypes.mute , null));
                correctlyDone = true;
            }
            else if(input > 0 && input <=othersNames.size() ){
                System.out.println("you mute player : " + othersNames.get(input - 1));
                actionCommand = new Command(CommandTypes.iDoMyAction , new PlayerAction(PlayersActionTypes.mute , othersNames.get(input -1 )));
                correctlyDone = true;
            }
            else {
                System.out.println("not valid input\nplease try again :");;
            }

        }
        if(! correctlyDone){
            System.out.println("time out , you didn't choose any one , so nobody will be muted tonight .");
            actionCommand = new Command(CommandTypes.iDoMyAction ,
                    new PlayerAction(PlayersActionTypes.mute ,
                    null));
        }

        try {
            getObjectOutputStream().writeObject(actionCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printNameArrayList(ArrayList<String> names){
        System.out.println("0- no one");
        for(int i = 1; i <= names.size(); i++){
            System.out.println(i + "- " +names.get(i-1));
        }
    }
}
