package rad.heydari.mohammad.midterm.project.mafia.roleThings;

import java.io.Serializable;

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

    public static boolean isEvil(RoleNames roleName){
        if(roleName == mafia || roleName == godFather || roleName == doctorLector){
            return true;
        }
        return false;
    }

}
