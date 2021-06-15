package rad.heydari.mohammad.midterm.project.mafia.night;

import java.io.Serializable;
/**
 * an enum for the types of the actions of the player
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public enum PlayersActionTypes implements Serializable {

    godFatherVictim,
    mafiaVictim,
    townDoctorSave,
    doctorLectorSave,
    detect,
    toughGuySaysShowDeadRoles,
    mute,
    professionalShoots,
    mayorCancelsTheVoting,

}
