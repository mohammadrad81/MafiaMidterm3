package rad.heydari.mohammad.midterm.project.mafia.night;

import rad.heydari.mohammad.midterm.project.mafia.roleThings.RoleNames;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys.Professional;
import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class NightEvents {

    private HashMap<ServerSidePlayerDetails , Integer> mafiaVictimsIntWeightHashMap;
    private HashSet<ServerSidePlayerDetails> savedOnes;
    private ArrayList<ServerSidePlayerDetails> dieTonight;
    private ServerSidePlayerDetails mutedOne;
    private ServerSidePlayerDetails professionalShoots;
    private boolean professionalDidWrongShoot;
    private boolean showDeadRoles;
    private int howManyTimesShowedDeadOnes = 0;
    private ServerSidePlayerDetails detectiveWantsToDetect;


    public NightEvents(){
        this.mafiaVictimsIntWeightHashMap = new HashMap<>();
        this.savedOnes = new HashSet<>();
        this.dieTonight = new ArrayList<>();
    }

    public void mafiaTakesVictim(ServerSidePlayerDetails victim, Boolean isGodFatherChoice){

        if(isGodFatherChoice){
            mafiaVictimsIntWeightHashMap.put(victim , 100000);
        }

        else{
            mafiaVictimsIntWeightHashMap.put(victim , 1);
        }

    }

    public void save(ServerSidePlayerDetails savedOne){
        savedOnes.add(savedOne);
    }

    public void mute(ServerSidePlayerDetails mutedOne){
        this.mutedOne = mutedOne;
    }

    public void professionalShoots(ServerSidePlayerDetails professionalVictim){
        if(RoleNames.isEvil(professionalVictim.getRoleName())){
            professionalShoots = professionalVictim;
            professionalDidWrongShoot = false;
        }
        else {
            professionalDidWrongShoot = true;
        }
    }

    public void toughGuySaysShowDeadRoles(){

        if(howManyTimesShowedDeadOnes < 2){
            showDeadRoles = true;
        }

    }

    public ArrayList<ServerSidePlayerDetails> getThoseWhoDieTonight(){

        ServerSidePlayerDetails mafiaVictim = getMafiaFinalChoice();
        if(! savedOnes.contains(mafiaVictim)){
            dieTonight.add(mafiaVictim);
        }

        if(professionalShoots != null){
            dieTonight.add(professionalShoots);
        }
        return dieTonight;
    }

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

    public boolean mustProfessionalDieForWrongShoot(){
        return professionalDidWrongShoot;
    }

    public boolean isShowDeadRoles() {
        return showDeadRoles;
    }

    public void resetNightEvents(){
        this.professionalDidWrongShoot = false;
        this.dieTonight = new ArrayList<>();
        this.professionalShoots = null;
        this.savedOnes = new HashSet<>();
        this.mafiaVictimsIntWeightHashMap = new HashMap<>();
        this.showDeadRoles = false;
        this.mutedOne = null;
    }

    public void setWhoDetectiveWantsToDetect(ServerSidePlayerDetails whoDetectiveWantsToDetect){
        this.detectiveWantsToDetect = whoDetectiveWantsToDetect;
    }

    public ServerSidePlayerDetails getWhoDetectiveWantsToDetect() {
        return detectiveWantsToDetect;
    }

    public ServerSidePlayerDetails getMutedOne() {
        return mutedOne;
    }

    public void setMutedOne(ServerSidePlayerDetails mutedOne) {
        this.mutedOne = mutedOne;
    }
}
