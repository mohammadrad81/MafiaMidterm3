package rad.heydari.mohammad.midterm.project.mafia.votingThings;

import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
/**
 * is a box to store the votes of players
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class VotingBox {

    private HashMap<ServerSidePlayerDetails , Integer> playerCountOfVotesHashMap;

    public VotingBox()
    {
        playerCountOfVotesHashMap = new HashMap<>();
    }

    public synchronized void saveVoting(ServerSidePlayerDetails suspect){

        Set<ServerSidePlayerDetails> keySet = playerCountOfVotesHashMap.keySet();
        if(keySet.contains(suspect)){
            playerCountOfVotesHashMap.put(suspect , playerCountOfVotesHashMap.get(suspect) + 1);
        }
        else {
            playerCountOfVotesHashMap.put(suspect , 1);
        }
    }

    /**
     * if at least half of the players voted the most voted player
     * and there is only one most voted player
     * the most voted player gets lynched
     * else no one gets lynched
     * @return the person who is about to lynch
     */
    public ServerSidePlayerDetails getTheLynchingPlayer(){
         Iterator<ServerSidePlayerDetails> iterator = playerCountOfVotesHashMap.keySet().iterator();
         ServerSidePlayerDetails playerDetails = null;
         int mostVotedNumber = getMostVotedNumber();
         if((2 * mostVotedNumber) >= playerCountOfVotesHashMap.size()) {
             if (isThereOnlyOneMostVotedPlayer(mostVotedNumber)) {
                 while (iterator.hasNext()) {
                     playerDetails = iterator.next();
                     if (playerCountOfVotesHashMap.get(playerDetails) == mostVotedNumber) {
                         return playerDetails;
                     }
                 }
             }
         }
         return null;
    }

    /**
     *
     * @return the votes of the most voted player
     */
    private int getMostVotedNumber(){
        int theMostVote = 0;

        Iterator<ServerSidePlayerDetails> iterator = playerCountOfVotesHashMap.keySet().iterator();
        ServerSidePlayerDetails playerDetails = null;
        while (iterator.hasNext()){
            playerDetails = iterator.next();
            if(playerCountOfVotesHashMap.get(playerDetails) > theMostVote){
                theMostVote = playerCountOfVotesHashMap.get(playerDetails);
            }
        }
        return theMostVote;
    }

    /**
     * checks if there is only one most voted player
     * @param mostVote is the most number of votes to a player
     * @return  true , if there is only one most voted player , else false
     */
    private boolean isThereOnlyOneMostVotedPlayer(int mostVote){
        Iterator<ServerSidePlayerDetails> iterator = playerCountOfVotesHashMap.keySet().iterator();
        ServerSidePlayerDetails player = null;
        int numberOfMostVotedPlayers = 0;
        while (iterator.hasNext()){
            player = iterator.next();
            if (playerCountOfVotesHashMap.get(player) == mostVote){
                numberOfMostVotedPlayers ++;
            }
        }

        if (numberOfMostVotedPlayers == 1){
            return true;
        }

        else {
            return false;
        }
    }

    /**
     * resets the box
     */
    public void resetTheBox(){
        playerCountOfVotesHashMap = new HashMap<>();
    }


}
