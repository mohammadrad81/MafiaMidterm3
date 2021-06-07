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
        System.out.println("who do you want to save ? ");
        System.out.println("0 - no one");
        ArrayList<String> savablePlayersNames = (ArrayList<String>) command.getCommandNeededThings();

        for(int i = 1 ; i <= savablePlayersNames.size() ; i++){
            System.out.println(i + "- " + savablePlayersNames.get(i-1));
        }

        int input = 0;
        while (!correctlyDone) {

            input = loopedTillRightInput.rangedIntInput(0, savablePlayersNames.size());

            if (input == 0) {
                command = new Command(CommandTypes.iDoMyAction,
                        new PlayerAction(PlayersActionTypes.save, null));
                correctlyDone = true;
            } else {
                if (savablePlayersNames.get(input - 1).equals(getUserName())) {
                    if (hasSavedHisSelf) {
                        System.out.println("you have saved yourself once");
                        System.out.println("choose another one : ");

                        for (int i = 1; i <= savablePlayersNames.size(); i++) {
                            System.out.println(i + "- " + savablePlayersNames.get(i - 1));
                        }
                        correctlyDone = false;
                    } else {
                        hasSavedHisSelf = true;
                        command = new Command(CommandTypes.iDoMyAction, new PlayerAction(PlayersActionTypes.save, getUserName()));
                        correctlyDone = true;
                    }
                } else {
                    command = new Command(CommandTypes.iDoMyAction,
                            new PlayerAction(PlayersActionTypes.save,
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
