package rad.heydari.mohammad.midterm.project.mafia.votingThings;

import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class VotingBox {

    private HashMap<ServerSidePlayerDetails , Integer> playerCountOfVotesHashMap;

    public VotingBox()
    {
        playerCountOfVotesHashMap = new HashMap<>();
    }

    public synchronized void saveVoting(ServerSidePlayerDetails suspect){

        if(suspect == null){
            return;
        }

        Set<ServerSidePlayerDetails> keySet = playerCountOfVotesHashMap.keySet();
        if(keySet.contains(suspect)){
            playerCountOfVotesHashMap.put(suspect , playerCountOfVotesHashMap.get(suspect) + 1);
        }
        else {
            playerCountOfVotesHashMap.put(suspect , 1 );
        }
    }

    public ServerSidePlayerDetails getMostVotedPlayer (){
         Iterator<ServerSidePlayerDetails> iterator = playerCountOfVotesHashMap.keySet().iterator();
         ServerSidePlayerDetails playerDetails = null;
         int mostVotedNumber = getMostVotedNumber();

         if (isThereOnlyOneMostVotedPlayer(mostVotedNumber)){
             while (iterator.hasNext()){
                 playerDetails = iterator.next();
                 if(playerCountOfVotesHashMap.get(playerDetails) == mostVotedNumber){
                    return playerDetails;
                 }
             }
         }

         return null;
    }

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

    public void resetTheBox(){
        playerCountOfVotesHashMap = new HashMap<>();
    }


}
