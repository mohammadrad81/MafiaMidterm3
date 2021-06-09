package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

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
    private LoopedTillRightInput loopedTillRightInput;
    public TownDoctor(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName) {
        super(objectInputStream, objectOutputStream , "town doctor" , userName);
        loopedTillRightInput = new LoopedTillRightInput();

    }

    @Override
    public void action(Command command) {
        Command saveCommand = null;
        boolean correctlyDone = false;
        ArrayList<String> savablePlayersNames = (ArrayList<String>) command.getCommandNeededThings();

        if(hasSavedHisSelf){
            savablePlayersNames.remove(getUserName());
        }

        int input = 0;
        while (!correctlyDone) {

            System.out.println("who do you want to save ? ");
            printStringArrayList(savablePlayersNames);
            input = loopedTillRightInput.rangedIntInput(0, savablePlayersNames.size());

            if (input == 0) {
                System.out.println("you save nobody tonight .");
                command = new Command(CommandTypes.iDoMyAction,
                        new PlayerAction(PlayersActionTypes.townDoctorSave, null));
                correctlyDone = true;
            }
            else {
                if (savablePlayersNames.get(input - 1).equals(getUserName())) {
                    System.out.println("you save " + savablePlayersNames.get(input - 1) + " ( yourself ) .");
                    hasSavedHisSelf = true;
                    command = new Command(CommandTypes.iDoMyAction, new PlayerAction(PlayersActionTypes.townDoctorSave, getUserName()));
                    correctlyDone = true;
                }
                else {
                    System.out.println("you save player : " + savablePlayersNames.get(input - 1) + "tonight .");
                    command = new Command(CommandTypes.iDoMyAction,
                            new PlayerAction(PlayersActionTypes.townDoctorSave,
                                    savablePlayersNames.get(input - 1)));
                    correctlyDone = true;
                }
            }
        }

        try {
            getObjectOutputStream().writeObject(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
