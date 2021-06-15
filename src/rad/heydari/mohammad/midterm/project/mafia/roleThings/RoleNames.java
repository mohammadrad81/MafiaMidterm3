package rad.heydari.mohammad.midterm.project.mafia.roleThings;

import java.io.Serializable;

/**
 * enum for storing the roles of the players ( server side )
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public enum RoleNames implements Serializable {

    // mafias :
    godFather,
    doctorLector,
    mafia,

    // people :
    mayor,
    normalCitizen,
    professional,
    therapist,
    toughGuy,
    townDoctor,
    detective;

    /**
     *
     * @param roleName is the roleName of the player
     * @return the role of the player as a string
     */
    public static String getRoleAsString(RoleNames roleName){
        if(roleName == godFather){
            return "godfather";
        }
        else if(roleName == doctorLector){
            return "doctor lector";
        }
        else if(roleName == mafia){
            return "mafia";
        }
        else if(roleName == mayor){
            return "mayor";
        }
        else if(roleName == normalCitizen){
            return "normal citizen";
        }
        else if(roleName == professional){
            return "professional";
        }
        else if(roleName == therapist){
            return "therapist";
        }
        else if(roleName == toughGuy){
            return "tough guy";
        }
        else if(roleName == townDoctor){
            return "town doctor";
        }
        else if(roleName == detective){
            return "detective";
        }
        else {
            return null;
        }
    }

    /**
     * checks if the role is an evil role or not
     * @param roleName is the roleName of the player
     * @return true if the player is evil , else false
     */
    public static boolean isEvil(RoleNames roleName){
        if(roleName == mafia || roleName == godFather || roleName == doctorLector){
            return true;
        }
        return false;
    }

}
