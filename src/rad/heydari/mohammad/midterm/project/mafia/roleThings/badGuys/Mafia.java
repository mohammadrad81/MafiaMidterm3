package rad.heydari.mohammad.midterm.project.mafia.roleThings.badGuys;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.BadGuys;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Mafia extends Actionable implements BadGuys {


    public Mafia(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        super(objectInputStream, objectOutputStream , "mafia");
    }

    @Override
    public void action(Command command) {

    }
}
