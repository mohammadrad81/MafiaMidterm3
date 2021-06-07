package rad.heydari.mohammad.midterm.project.mafia.roleThings.badGuys;

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

public class GodFather extends Actionable implements GoodGuys {
    private boolean hasSavedHimSelf;
    private Scanner scanner ;
    public GodFather(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName) {
        super(objectInputStream, objectOutputStream , "godfather" , userName);
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
            System.out.println("choose someone to kill tonight (as godfather of the mafia) :");
            printStringArrayList(othersNames);
            input = scanner.nextInt();

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
        try {
            getObjectOutputStream().writeObject(actionCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
