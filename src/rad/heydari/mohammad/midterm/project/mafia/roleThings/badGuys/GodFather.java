package rad.heydari.mohammad.midterm.project.mafia.roleThings.badGuys;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.GoodGuys;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GodFather extends Actionable implements GoodGuys {
    private boolean hasSavedHimSelf;

    public GodFather(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        super(objectInputStream, objectOutputStream , "godfather");
    }

    @Override
    public void action(Command command) {

    }
}
