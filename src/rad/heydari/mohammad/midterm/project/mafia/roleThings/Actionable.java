package rad.heydari.mohammad.midterm.project.mafia.roleThings;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class Actionable implements Role {

    private String roleNameString;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Actionable(ObjectInputStream objectInputStream , ObjectOutputStream objectOutputStream , String roleNameString){
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.roleNameString = roleNameString;
    }

    public abstract void action(Command command);

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    @Override
    public String getRoleNameString() {
        return roleNameString;
    }

}