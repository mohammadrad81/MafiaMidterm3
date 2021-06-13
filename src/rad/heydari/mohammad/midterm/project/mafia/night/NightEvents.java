
package rad.heydari.mohammad.midterm.project.mafia.night;

import rad.heydari.mohammad.midterm.project.mafia.roleThings.RoleNames;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
/**
 * a class to store the events in night and conclude from them
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class NightEvents {

    private HashMap<ServerSidePlayerDetails , Integer> mafiaVictimsIntWeightHashMap;
    private HashSet<ServerSidePlayerDetails> townDoctorSaves;
    private HashSet<ServerSidePlayerDetails> lectorSaves;
    private ArrayList<ServerSidePlayerDetails> dieTonight;
    private ServerSidePlayerDetails mutedOne;
    private ServerSidePlayerDetails professionalShoots;
    private boolean professionalDidWrongShoot;
    private boolean showDeadRoles;
    private int howManyTimesShowedDeadOnes = 0;
    private ServerSidePlayerDetails detectiveWantsToDetect;

    /**
     * constructor , initializes the events
     */
    public NightEvents(){
        this.mafiaVictimsIntWeightHashMap = new HashMap<>();
        this.townDoctorSaves = new HashSet<>();
        this.lectorSaves = new HashSet<>();
        this.dieTonight = new ArrayList<>();
    }

    /**
     * mafia takes a victim
     * @param victim is the person the mafia member chooses
     * @param isGodFatherChoice if the godfather chooses , true , else , false
     */
    public void mafiaTakesVictim(ServerSidePlayerDetails victim, Boolean isGodFatherChoice){

        if(isGodFatherChoice){
            mafiaVictimsIntWeightHashMap.put(victim , 100000);
        }

        else{
            mafiaVictimsIntWeightHashMap.put(victim , 1);
        }

    }

    /**
     * town doctor saves a player
     * @param playerDetails is the saving player
     */
    public void townDoctorSave(ServerSidePlayerDetails playerDetails){
        townDoctorSaves.add(playerDetails);
    }

    /**
     * doctor lector saves a player
     * @param playerDetails is the saving player
     */
    public void lectorSave(ServerSidePlayerDetails playerDetails){
        lectorSaves.add(playerDetails);
    }

    /**
     * therapist mutes a player
     * @param mutedOne is the muted player
     */
    public void mute(ServerSidePlayerDetails mutedOne){
        this.mutedOne = mutedOne;
    }

    /**
     * professional shoots a player
     * @param professionalVictim is the player , the professional shoots
     */
    public void professionalShoots(ServerSidePlayerDetails professionalVictim){
        if(professionalVictim == null){
            professionalDidWrongShoot = false;
        }
        if(RoleNames.isEvil(professionalVictim.getRoleName())){
            professionalShoots = professionalVictim;
            professionalDidWrongShoot = false;
        }
        else {
            professionalDidWrongShoot = true;
        }
    }

    /**
     * tough Guy wants the dead roles to be revealed for everyone
     */
    public void toughGuySaysShowDeadRoles(){
        if(howManyTimesShowedDeadOnes < 2){
            showDeadRoles = true;
            howManyTimesShowedDeadOnes++;
        }
    }

    /**
     *
     * @return an arrayList of those who must die tonight , ( not handling the wrong shoot of the professional )
     */
    public ArrayList<ServerSidePlayerDetails> getThoseWhoDieTonight(){

        ServerSidePlayerDetails mafiaVictim = getMafiaFinalChoice();
        if(! townDoctorSaves.contains(mafiaVictim)){
            dieTonight.add(mafiaVictim);
        }

        if(professionalShoots != null && !lectorSaves.contains(professionalShoots)){
            dieTonight.add(professionalShoots);
        }

        return dieTonight;
    }

    /**
     *
     * @return the final choice of the mafia
     */
    private ServerSidePlayerDetails getMafiaFinalChoice (){
        int maxWeight = 0;
        ServerSidePlayerDetails finalChoice = null;
        ServerSidePlayerDetails victim = null;
        Iterator<ServerSidePlayerDetails> mafiaVictimsIterator = mafiaVictimsIntWeightHashMap.keySet().iterator();
        while (mafiaVictimsIterator.hasNext()){
            victim = mafiaVictimsIterator.next();
            if(maxWeight <= mafiaVictimsIntWeightHashMap.get(victim)){
                maxWeight = mafiaVictimsIntWeightHashMap.get(victim);
                finalChoice = victim;
            }
        }
        return finalChoice;
    }

    /**
     * if the professional shoots the wrong guy , he dies
     * @return true if the shot was wrong , else false
     */
    public boolean mustProfessionalDieForWrongShoot(){
        return professionalDidWrongShoot;
    }

    /**
     *
     * @return true if the tough guy wants to reveal dead roles , else false
     */
    public boolean isShowDeadRoles() {
        return showDeadRoles;
    }

    /**
     * resets the events
     */
    public void resetNightEvents(){
        this.professionalDidWrongShoot = false;
        this.dieTonight = new ArrayList<>();
        this.professionalShoots = null;
        this.townDoctorSaves = new HashSet<>();
        this.lectorSaves = new HashSet<>();
        this.mafiaVictimsIntWeightHashMap = new HashMap<>();
        this.showDeadRoles = false;
        this.mutedOne = null;
        this.detectiveWantsToDetect = null;
    }

    /**
     *
     * @param whoDetectiveWantsToDetect is the player , the detective wants to detect
     */
    public void setWhoDetectiveWantsToDetect(ServerSidePlayerDetails whoDetectiveWantsToDetect){
        this.detectiveWantsToDetect = whoDetectiveWantsToDetect;
    }

    /**
     *
     * @return the player , the detective wants to detect
     */
    public ServerSidePlayerDetails getWhoDetectiveWantsToDetect() {
        return detectiveWantsToDetect;
    }

    /**
     *
     * @return the muted player
     */
    public ServerSidePlayerDetails getMutedOne() {
        return mutedOne;
    }
}