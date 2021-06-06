package rad.heydari.mohammad.midterm.project.mafia.gameThings;

import rad.heydari.mohammad.midterm.project.mafia.serverThings.ServerSidePlayerDetails;

import java.util.HashSet;

public class GameState {

    private int alivePlayersCount;
    private int aliveMafiasCount;
    private HashSet<ServerSidePlayerDetails> serverSidePlayersDetails;
    private HashSet<ServerSidePlayerDetails> deadOnes;

    public GameState(int alivePlayersCount, int aliveMafiasCount) {
        this.alivePlayersCount = alivePlayersCount;
        this.aliveMafiasCount = aliveMafiasCount;
        serverSidePlayersDetails = new HashSet<>();
        deadOnes = new HashSet<>();
    }

    public void addPlayerDetails(ServerSidePlayerDetails serverSidePlayerDetails){
        this.serverSidePlayersDetails.add(serverSidePlayerDetails);
    }

    public void removePlayerDetails(ServerSidePlayerDetails serverSidePlayerDetails){
        this.serverSidePlayersDetails.remove(serverSidePlayerDetails);
    }
}
