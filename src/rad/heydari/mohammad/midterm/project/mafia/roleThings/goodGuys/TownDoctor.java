package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.LoopedTillRightInput;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.GoodGuys;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class TownDoctor extends Actionable implements GoodGuys {

    private boolean hasSavedHisSelf;
    private LoopedTillRightInput loopedTillRightInput;

    public TownDoctor(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        super(objectInputStream, objectOutputStream , "town doctor");
        loopedTillRightInput = new LoopedTillRightInput();
    }

    @Override
    public void action(Command command) {
        System.out.println("who do you want to save ? ");
        System.out.println("0 - no one");
        ArrayList<String> savablePlayersNames = (ArrayList<String>) command.getCommandNeededThings();

        for(int i = 1 ; i <= savablePlayersNames.size() ; i++){
            System.out.println(i + "- " + savablePlayersNames.get(i-1));
        }

    }
}
