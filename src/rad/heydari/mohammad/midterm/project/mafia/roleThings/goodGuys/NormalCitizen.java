package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

import rad.heydari.mohammad.midterm.project.mafia.roleThings.GoodGuys;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NormalCitizen implements GoodGuys {
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String roleNameString;
    public NormalCitizen(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream ) {
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.roleNameString = "normal citizen";
    }

    @Override
    public String getRoleNameString() {
        return null;
    }
}
