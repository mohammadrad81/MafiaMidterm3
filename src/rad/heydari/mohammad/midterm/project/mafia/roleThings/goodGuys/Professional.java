package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

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

public class Professional extends Actionable implements GoodGuys {

    private Scanner scanner;

    public Professional(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName) {
        super(objectInputStream, objectOutputStream , "professional" , userName);
        scanner = new Scanner(System.in);
    }


    @Override
    public void action(Command command) {
        boolean correctlyDone = false;
        ArrayList<String> othersNames = (ArrayList<String>) command.getCommandNeededThings();
        othersNames.remove(getUserName());

        int input = 0;
        Command actionCommand = null;

        while (!correctlyDone){
            System.out.println("choose somebody to shoot ( be careful , if you shoot wrong guy , you die instead ) : ");
            printStringArrayList(othersNames);

            input = scanner.nextInt();
            if(input == 0){
                actionCommand = new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.professionalShoots ,
                                null));
            correctlyDone = true;
            }

            else if(input > 0 && input <= othersNames.size()){
                actionCommand = new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.professionalShoots ,
                        othersNames.get(input - 1)));
                correctlyDone = true;
            }
            else {
                System.out.println("not valid input\n please try again :");
            }

        }

        try {
            getObjectOutputStream().writeObject(actionCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
