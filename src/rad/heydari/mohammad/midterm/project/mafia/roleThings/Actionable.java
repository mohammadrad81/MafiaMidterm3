package rad.heydari.mohammad.midterm.project.mafia.roleThings;

import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public abstract class Actionable implements Role {
    private String userName;
    private String roleNameString;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Actionable(ObjectInputStream objectInputStream ,
                      ObjectOutputStream objectOutputStream ,
                      String roleNameString,
                      String userName){
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.roleNameString = roleNameString;
        this.userName = userName;
    }

    public abstract void action(Command command);

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String getRoleNameString() {
        return roleNameString;
    }

    public void printStringArrayList(ArrayList<String> names){
        System.out.println("0- no one");
        for(int i = 1; i <= names.size(); i++){
            System.out.println(i + "- " +names.get(i-1));
        }
    }
}