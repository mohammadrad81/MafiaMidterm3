package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.GoodGuys;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * class for the role , NormalCitizen
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class NormalCitizen implements GoodGuys {
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String roleNameString;
    /**
     * constructor for the role
     * @param objectInputStream the InputStream to communicate with server
     * @param objectOutputStream the outputStream to communicate with server
     */
    public NormalCitizen(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.roleNameString = "normal citizen";
    }

    @Override
    public String getRoleString() {
        return roleNameString;
    }
}
