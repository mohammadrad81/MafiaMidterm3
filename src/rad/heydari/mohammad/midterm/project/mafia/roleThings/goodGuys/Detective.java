package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Detective extends Actionable {

    public Detective(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        super(objectInputStream, objectOutputStream, "detective");
    }

    @Override
    public void action(Command command) {

    }

}
