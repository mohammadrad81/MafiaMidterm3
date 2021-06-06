package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.GoodGuys;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Therapist extends Actionable implements GoodGuys {

    public Therapist(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        super(objectInputStream, objectOutputStream , "therapist");
    }

    @Override
    public void action(Command command) {

    }
}
