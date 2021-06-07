package rad.heydari.mohammad.midterm.project.mafia.roleThings.badGuys;

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

public class Mafia extends Actionable implements BadGuys {
    private Scanner scanner;

    public Mafia(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream ,String userName) {
        super(objectInputStream, objectOutputStream , "mafia" , userName);
        scanner = new Scanner(System.in);
    }

    @Override
    public void action(Command command) {
        boolean correctlyDone = false;
        ArrayList<String> othersNames = (ArrayList<String>) command.getCommandNeededThings();

        othersNames.remove(getUserName());

        int input = 0;
        Command actionCommand = null;

        while (! correctlyDone){
            System.out.println("choose who to die tonight (as a mafia) : ");
            printStringArrayList(othersNames);

            input = scanner.nextInt();

            if(input == 0){
                System.out.println("you have chosen no one to kill tonight");
                actionCommand = new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.mafiaVictim ,
                        null));
                correctlyDone = true;
            }

            else if(input > 0 && input <= othersNames.size()){
                System.out.println(" you choose to kill " + othersNames.get(input - 1));
                actionCommand = new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.mafiaVictim ,
                                othersNames.get(input - 1 )));
                correctlyDone = true;
            }
            else {
                System.out.println("not valid input\nplease try again : ");
            }
        }

        try {
            getObjectOutputStream().writeObject(actionCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
