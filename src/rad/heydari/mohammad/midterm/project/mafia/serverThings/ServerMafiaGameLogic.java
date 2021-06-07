package rad.heydari.mohammad.midterm.project.mafia.serverThings;

import rad.heydari.mohammad.midterm.project.mafia.MafiaGameException.RepetitiousUserNameException;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables.*;
import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.gameThings.ServerSideGame;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayerAction;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayersActionTypes;
import rad.heydari.mohammad.midterm.project.mafia.night.NightEvents;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.RoleNames;
import rad.heydari.mohammad.midterm.project.mafia.votingThings.Vote;
import rad.heydari.mohammad.midterm.project.mafia.votingThings.VotingBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerMafiaGameLogic implements ServerSideGame {
    private ArrayList<ServerSidePlayerDetails> alivePlayers;
    private ArrayList<ServerSidePlayerDetails> spectators;
    private ArrayList<ServerSidePlayerDetails> offlineDeadOnes;
    private VotingBox votingBox;
    private NightEvents nightEvents;
    private boolean toughGuyIsHurt;

    public ServerMafiaGameLogic(ArrayList<ServerSidePlayerDetails> alivePlayers){
        this.spectators = new ArrayList<>();
        this.offlineDeadOnes = new ArrayList<>();
        this.alivePlayers = alivePlayers;
        this.votingBox = new VotingBox();
        this.nightEvents = new NightEvents();
    }
    @Override
    public void startTheGame(){

        gameStarting();

        gameEnding();

    }

    private void gameStarting(){
        boolean playersHaveBeenIntroduced = false;

        initUserNames();

        waitForAllToGetReady();

        givePlayersTheirRoles();


        while (true){ // it should be !isGameOver()

            if(! playersHaveBeenIntroduced){
                introductionNight();
                playersHaveBeenIntroduced = true;
            }

            else {
                night();
            }

            day();

            voting();

        }


    }

    private void gameEnding(){

    }

    private void initUserNames(){
        Command determineYourUserName = new Command(CommandTypes.determineYourUserName , null);

        sendCommandToOnlinePlayers(determineYourUserName);

        ExecutorService executorService = Executors.newCachedThreadPool();

        ServerSidePlayerDetails player = null;

        Iterator<ServerSidePlayerDetails> playerIterator = alivePlayers.iterator();

        while (playerIterator.hasNext()){
            player = playerIterator.next();
            executorService.execute(new Thread(new RunnableUserNameTaker(this ,player)));
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(5 , TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void givePlayersTheirRoles(){

        shuffleThePlayers();

        Iterator<ServerSidePlayerDetails> playerIterator = alivePlayers.iterator();
        int counter = 1;
        while (playerIterator.hasNext()){


            ServerSidePlayerDetails player = playerIterator.next();
            if(counter == 1){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.godFather);
                    player.setRoleName(RoleNames.godFather);
                } catch (IOException e) {
                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }

            else if(counter == 2){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.townDoctor);
                    player.setRoleName(RoleNames.townDoctor);
                } catch (IOException e) {
                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }

            else if(counter == 3){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.detective);
                    player.setRoleName(RoleNames.detective);
                } catch (IOException e) {
                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }

            else if(counter == 4){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.professional);
                    player.setRoleName(RoleNames.professional);
                } catch (IOException e) {
                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }
            else if(counter == 5){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.therapist);
                    player.setRoleName(RoleNames.therapist);
                } catch (IOException e) {
                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }
            else if(counter == 6){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.doctorLector);
                    player.setRoleName(RoleNames.doctorLector);
                } catch (IOException e) {
                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                   continue;
                }
            }
            else if(counter == 7){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.mayor);
                    player.setRoleName(RoleNames.mayor);
                } catch (IOException e) {
                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }
            else if(counter == 8){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.toughGuy);
                    player.setRoleName(RoleNames.toughGuy);
                } catch (IOException e) {
                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }
            else if(counter %3 == 0){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.mafia);
                    player.setRoleName(RoleNames.mafia);
                } catch (IOException e) {
                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }
            else {
                try {
                    player.sendTheRoleToThePlayer(RoleNames.normalCitizen);
                    player.setRoleName(RoleNames.normalCitizen);
                } catch (IOException e) {
                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }

            counter ++;
        }

    }

    private void shuffleThePlayers(){

        ArrayList<ServerSidePlayerDetails> shuffledPlayers = new ArrayList<>();
        Random random = new Random();
        int randomNumber = 0;
        while (alivePlayers.size() > 0){
            randomNumber = random.nextInt(alivePlayers.size());
            shuffledPlayers.add(alivePlayers.get(randomNumber));
            alivePlayers.remove(randomNumber);
        }
        alivePlayers = shuffledPlayers;

    }

    public void doTheCommand(Command command){
        syncDoTheCommand(command);
    }

    private synchronized void syncDoTheCommand(Command command){// implementLater

        if(command.getType() == CommandTypes.messageToOthers){
            sendMessageToAll((Message) command.getCommandNeededThings());
        }

        else if(command.getType() == CommandTypes.iVote){
            Vote vote = (Vote) command.getCommandNeededThings();
            votingBox.saveVoting(getPlayerByName(vote.getSuspectName()));
            notifyOthersThisVote(vote);
        }

        else if(command.getType() == CommandTypes.iDoMyAction){

            PlayerAction playerAction =(PlayerAction) command.getCommandNeededThings();
            if(playerAction.getNightActionType() == PlayersActionTypes.mafiaVictim){
                if(playerAction.getNameOfThePlayerActionHappensTo() != null){
                    synchronized (nightEvents){
                        nightEvents.mafiaTakesVictim(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()),
                                false);
                    }
                }
            }

            else if(playerAction.getNightActionType() == PlayersActionTypes.godFatherVictim){
                synchronized (nightEvents){
                    nightEvents.mafiaTakesVictim(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()),
                            true);
                }
            }

            else if(playerAction.getNightActionType() == PlayersActionTypes.save){
                if(playerAction.getNameOfThePlayerActionHappensTo() != null){
                    synchronized (nightEvents){
                        nightEvents.save(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
                    }
                }
            }

            else if(playerAction.getNightActionType() == PlayersActionTypes.toughGuySaysShowDeadRoles){
                synchronized (nightEvents){
                    nightEvents.toughGuySaysShowDeadRoles();
                }
            }

            else if(playerAction.getNightActionType() == PlayersActionTypes.detect){
                synchronized (nightEvents){
                    nightEvents.setWhoDetectiveWantsToDetect(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
                }
            }

        }
        // other things :
    }

    private void notifyOthersThisVote(Vote vote){

        String voteDescription = "voting : " + vote.getVoterName() + " voted " + vote.getSuspectName();
        Command someOneVoted = new Command(CommandTypes.serverToClientString , voteDescription);
        sendCommandToOnlineAndSpectatorPlayers(someOneVoted);

    }

    public synchronized void setUserName(ServerSidePlayerDetails playerDetails , String userName) throws RepetitiousUserNameException {
        if(isUserNameRepetitious(userName)){
            throw new RepetitiousUserNameException("this userName is already taken");
        }
        else {
            playerDetails.setUserName(userName);
        }
    }

    private  boolean isUserNameRepetitious(String userName){
        Iterator<ServerSidePlayerDetails> serverSidePlayerDetailsIterator = alivePlayers.iterator();
        ServerSidePlayerDetails serverSidePlayerDetails = null;
        while (serverSidePlayerDetailsIterator.hasNext()){
            serverSidePlayerDetails = serverSidePlayerDetailsIterator.next();
            if(userName.equals(serverSidePlayerDetails.getUserName())){
                return true;
            }
        }



        return false;
    }

//    private void deleteThePlayersWithoutUserNames(){
//        Iterator<ServerSidePlayerDetails> iterator = playerDetailsArrayList.iterator();
//        while (iterator.hasNext()){
//            if(iterator.hasNext()){
//                ServerSidePlayerDetails serverSidePlayerDetails = iterator.next();
//                if(serverSidePlayerDetails.getUserName() == null){
//                    iterator.remove();
//                }
//            }
//        }
//    }

    private void introductionNight(){

        mafiaIntroduction();

        townDoctorAndMayorIntroduction();

    }

    private void mafiaIntroduction(){

        String mafiaIntroduction = getMafiaIntroduction();
        Command mafiaIntroductionCommand = new Command(CommandTypes.serverToClientString, mafiaIntroduction);
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
        ServerSidePlayerDetails serverSidePlayerDetails = null;
        while (playerDetailsIterator.hasNext()){

            serverSidePlayerDetails = playerDetailsIterator.next();
            if(serverSidePlayerDetails.getRoleName() == RoleNames.mafia ||
                    serverSidePlayerDetails.getRoleName() == RoleNames.doctorLector ||
                    serverSidePlayerDetails.getRoleName() == RoleNames.godFather){

                try {
                    serverSidePlayerDetails.sendCommandToPlayer(mafiaIntroductionCommand);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }



    }

    private void townDoctorAndMayorIntroduction(){

//        String mayorToDoctorIntroduction;
//        ServerSidePlayerDetails doctor = findSpecificAliveRolePlayer(RoleNames.townDoctor);
        ServerSidePlayerDetails mayor = findSpecificAliveRolePlayer(RoleNames.mayor);

//        if(doctor != null){
//            try {
//                doctor.sendCommandToPlayer(new Command(CommandTypes.serverToClientString , getMayorToDoctorIntroduction()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        if(mayor != null){
            try {
                mayor.sendCommandToPlayer(new Command(CommandTypes.serverToClientString , getDoctorToMayorIntroduction()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String  getMafiaIntroduction(){
        String mafiaIntroduction = "the list of mafias : \nthe godfather : " ;

        if(findSpecificAliveRolePlayer(RoleNames.godFather) != null){
            mafiaIntroduction += findSpecificAliveRolePlayer(RoleNames.godFather).getUserName() + "\n";
        }
        else {
            mafiaIntroduction += "no one\n";
        }

        mafiaIntroduction += "doctor lector : ";

        if(findSpecificAliveRolePlayer(RoleNames.doctorLector) != null){
            mafiaIntroduction += findSpecificAliveRolePlayer(RoleNames.doctorLector).getUserName() + "\n";
        }
        else{
            mafiaIntroduction += "no one\n";
        }
        mafiaIntroduction += "mafia : ";

        if(findSameRolesPlayers(RoleNames.mafia).size() == 0){
            mafiaIntroduction += "no one";
        }
        else {
            Iterator<ServerSidePlayerDetails> playerDetailsIterator = findSameRolesPlayers(RoleNames.mafia).iterator();
            ServerSidePlayerDetails player = null;
            while (playerDetailsIterator.hasNext()){
                player = playerDetailsIterator.next();
                mafiaIntroduction += player.getUserName() + "\n";
            }
        }
        return mafiaIntroduction;
    }

    private ServerSidePlayerDetails findSpecificAliveRolePlayer(RoleNames roleName){
        Iterator<ServerSidePlayerDetails> iterator = alivePlayers.iterator();

        while (iterator.hasNext()){
            ServerSidePlayerDetails player = iterator.next();

            if(player.getRoleName() == roleName){
                return player;
            }
        }
        return null;
    }

    private ServerSidePlayerDetails findSpecificRolePlayerNoMatterOnOrOff(RoleNames roleName){
        if(findSpecificAliveRolePlayer(roleName) != null){
            return findSpecificAliveRolePlayer(roleName);
        }
        else {
            Iterator<ServerSidePlayerDetails> playerDetailsIterator = spectators.iterator();
            ServerSidePlayerDetails serverSidePlayerDetails = null;
            while (playerDetailsIterator.hasNext()){
                serverSidePlayerDetails = playerDetailsIterator.next();
                if(serverSidePlayerDetails.getRoleName() == roleName){
                    return serverSidePlayerDetails;
                }
            }

            playerDetailsIterator = offlineDeadOnes.iterator();
            while (playerDetailsIterator.hasNext()){
                serverSidePlayerDetails = playerDetailsIterator.next();
                if(serverSidePlayerDetails.getRoleName() == roleName){
                    return serverSidePlayerDetails;
                }
            }
        }

        return null;

    }

    private ArrayList<ServerSidePlayerDetails> findSameRolesPlayers(RoleNames roleName){
        Iterator<ServerSidePlayerDetails> iterator = alivePlayers.iterator();

        ArrayList<ServerSidePlayerDetails> sameRolePlayers = new ArrayList<>();
        while (iterator.hasNext()){
            ServerSidePlayerDetails playerDetails = iterator.next();
            if(playerDetails.getRoleName() == roleName){
                sameRolePlayers.add(playerDetails);

            }
        }

        return sameRolePlayers;
    }

    private String getMayorToDoctorIntroduction(){
        String mayorToDoctorIntroduction = "the mayor is : ";
        ServerSidePlayerDetails mayor = findSpecificAliveRolePlayer(RoleNames.mayor);
        if(mayor == null){
            mayorToDoctorIntroduction += "no one";
        }
        else {
            mayorToDoctorIntroduction += mayor.getUserName();
        }
        return mayorToDoctorIntroduction;
    }

    private String getDoctorToMayorIntroduction(){
        String doctorToMayorIntroduction = "the doctor is : ";
        ServerSidePlayerDetails doctor = findSpecificAliveRolePlayer(RoleNames.townDoctor);

        if(doctor == null){
                doctorToMayorIntroduction += "no one";
        }
        else {
            doctorToMayorIntroduction += doctor.getUserName();
        }

        return doctorToMayorIntroduction;
    }

    public void removeOfflinePlayerNotifyOthers(ServerSidePlayerDetails removingPlayer){

        notifyOthersThePlayerGotOffline(removingPlayer);


        if(alivePlayers.contains(removingPlayer)){
            offlineDeadOnes.add(removingPlayer);
            alivePlayers.remove(removingPlayer);
        }
    }

    private void notifyOthersThePlayerGotOffline(ServerSidePlayerDetails offlinePlayer){

        Iterator<ServerSidePlayerDetails> iterator = alivePlayers.iterator();

        while (iterator.hasNext()){
            ServerSidePlayerDetails messageReceiver = iterator.next();
            try {
                messageReceiver.sendCommandToPlayer(new Command(CommandTypes.serverToClientString ,
                                        " the player : " + offlinePlayer.getUserName()  + " left the game ! "));
            } catch (IOException e) {
                e.printStackTrace();
                removeOfflinePlayerNotifyOthers(messageReceiver); // if the message receiver is offline too
            }
        }
    }

    private boolean isGameOver(){

        if(getBadGuysNumber() >= getGoodGuysNumber()){
            return true;
        }

        return false;

    }

    private int getBadGuysNumber(){
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
        RoleNames playerRoleName = null;
        int badGuysNumber = 0;
        while (playerDetailsIterator.hasNext()){
            playerRoleName = playerDetailsIterator.next().getRoleName();

            if(playerRoleName == RoleNames.godFather ||
                    playerRoleName == RoleNames.doctorLector ||
                    playerRoleName == RoleNames.mafia){
                badGuysNumber ++;
            }
        }

        return badGuysNumber;
    }

    private int getGoodGuysNumber(){
            Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
            RoleNames playerRoleName = null;
            int goodGuysNumber = 0;
            while (playerDetailsIterator.hasNext()){
                playerRoleName = playerDetailsIterator.next().getRoleName();

                if(playerRoleName != RoleNames.godFather &&
                        playerRoleName != RoleNames.doctorLector &&
                        playerRoleName != RoleNames.mafia){

                    goodGuysNumber ++;
                }
            }

            return goodGuysNumber;
    }

    private void waitForAllToGetReady(){

        ExecutorService executorService = Executors.newCachedThreadPool();

        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();

        while(playerDetailsIterator.hasNext()){
            executorService.execute(new Thread(new RunnableWaitToGetReady(playerDetailsIterator.next())));
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(1 , TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void night(){

        nightEvents.resetNightEvents();

        informAllPlayersItsNightDoYourAction();

        runTheNightHandlers();

        revealTheDetectedPlayerForDetective();

        killThoseWhoDiedTonight();

        if(nightEvents.isShowDeadRoles()){
            revealDeadRoles();
        }

    }

    private void day(){
        tellEveryOneItsChattingTime();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        while (playerDetailsIterator.hasNext()){
            player = playerDetailsIterator.next();
            executorService.execute(new RunnableServerSideMessageReceiver(player));
        }
        executorService.shutdown();

        try {
            executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tellEveryOneChatIsOver();
    }

    private void tellEveryOneItsChattingTime(){
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        Command chattingTimeCommand = new Command(CommandTypes.chatRoomStarted,  null);
        while (playerDetailsIterator.hasNext()){
            player = playerDetailsIterator.next();
            try {
                player.sendCommandToPlayer(chattingTimeCommand);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Iterator<ServerSidePlayerDetails> spectatorsIterator = spectators.iterator();
        ServerSidePlayerDetails spectator = null;
        while (spectatorsIterator.hasNext()){
            spectator = spectatorsIterator.next();

            try {
                spectator.sendCommandToPlayer(chattingTimeCommand);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void tellEveryOneChatIsOver(){
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        Command chattingTimeCommand = new Command(CommandTypes.chatRoomIsClosed,  null);

        while (playerDetailsIterator.hasNext()){
            player = playerDetailsIterator.next();
            try {
                player.sendCommandToPlayer(chattingTimeCommand);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        Iterator<ServerSidePlayerDetails> spectatorsIterator = spectators.iterator();
        ServerSidePlayerDetails spectator = null;
        while (spectatorsIterator.hasNext()){
            spectator = spectatorsIterator.next();

            try {
                spectator.sendCommandToPlayer(chattingTimeCommand);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void voting(){
        Command votingResult;
        informAllPlayersItsVotingTime();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Iterator<ServerSidePlayerDetails> onlinePlayersIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;


        while (onlinePlayersIterator.hasNext()){
            player = onlinePlayersIterator.next();
            executorService.execute(new Thread(new RunnableVoteHandler(player)));
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(5 , TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        if(votingBox.getMostVotedPlayer() == null){
            votingResult = new Command(CommandTypes.votingResult , " no body is lynched today ");

            sendCommandToOnlineAndSpectatorPlayers(votingResult);

        }

        else {
            ServerSidePlayerDetails mostVotedPlayer = votingBox.getMostVotedPlayer();
            votingResult = new Command(CommandTypes.votingResult ,
                    mostVotedPlayer.getUserName() +
                            " is lynched today , his/her role was : " +
                            RoleNames.getRoleAsString(mostVotedPlayer.getRoleName()));


            sendCommandToOnlineAndSpectatorPlayers(votingResult);

            removePlayerFromOnlinePlayersToSpectators(mostVotedPlayer);

            try {
                mostVotedPlayer.sendCommandToPlayer(new Command(CommandTypes.youAreDead , " you are lynched "));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



        votingBox.resetTheBox();

    }

    private void informAllPlayersItsVotingTime(){
        Command votingTimeInform = new Command(CommandTypes.vote , getAlivePlayersUserNames());
        sendCommandToOnlineAndSpectatorPlayers(votingTimeInform);
    }

//    private void informAllPlayersVotingTimeIsOver(){
//        Command votingIsOver = new Command(CommandTypes. , null);
//        sendCommandToOnlineAndSpectatorPlayers(votingIsOver);
//    }

    private ArrayList<String> getAlivePlayersUserNames(){

        Iterator<ServerSidePlayerDetails> iterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        ArrayList<String> playersNames = new ArrayList<>();

        while (iterator.hasNext()){
            player = iterator.next();
            playersNames.add(player.getUserName());
        }

        return playersNames;
    }

    private ArrayList<String> getAliveBadGuysNames(){
        ArrayList<String> aliveBadGuysNames = new ArrayList<>();
        Iterator<ServerSidePlayerDetails> playersIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        synchronized (alivePlayers){
            while (playersIterator.hasNext()){
                player = playersIterator.next();
                if (RoleNames.isEvil(player.getRoleName())){
                    aliveBadGuysNames.add(player.getUserName());
                }
            }
        }

        return aliveBadGuysNames;

    }

    public void sendMessageToAll(Message message){
        // sending to alive players :

        Iterator<ServerSidePlayerDetails> iterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;

        Command command = new Command(CommandTypes.newMessage , message);
        while (iterator.hasNext()){
            player = iterator.next();

            try {
                player.sendCommandToPlayer(command);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //sending message to the spectators ( dead online players ) :

        Iterator<ServerSidePlayerDetails> spectatorsIterator = spectators.iterator();
        while (spectatorsIterator.hasNext()){
            player = spectatorsIterator.next();

            try {
                player.sendCommandToPlayer(command);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private ServerSidePlayerDetails getPlayerByName(String userName){
        Iterator<ServerSidePlayerDetails> iterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;

        while (iterator.hasNext()){
            player = iterator.next();
            if(player.getUserName().equals(userName)){
                return player;
            }
        }

        iterator = spectators.iterator();
        while (iterator.hasNext()){
            player = iterator.next();
            if(player.getUserName().equals(userName)){
                return player;
            }
        }
        iterator = offlineDeadOnes.iterator();
        while (iterator.hasNext()){
            player = iterator.next();
            if (player.getUserName().equals(userName)){
                return player;
            }
        }

        return null;
    }

    private synchronized void sendCommandToOnlinePlayers(Command command){
        Iterator<ServerSidePlayerDetails> playerIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;

        while (playerIterator.hasNext()){

            player = playerIterator.next();

            try {
                player.sendCommandToPlayer(command);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendCommandToOnlineAndSpectatorPlayers(Command command){
        sendCommandToOnlinePlayers(command);
        sendCommandToSpectators(command);
    }

    private void sendCommandToSpectators(Command command){
        Iterator<ServerSidePlayerDetails> playerIterator = spectators.iterator();
        ServerSidePlayerDetails player = null;
        while (playerIterator.hasNext()){

            player = playerIterator.next();
            try {
                player.sendCommandToPlayer(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void removePlayerFromOnlinePlayersToSpectators(ServerSidePlayerDetails player){

        synchronized (alivePlayers){
            alivePlayers.remove(player);
        }

        synchronized (spectators){
            spectators.add(player);
        }

    }

    private void informAllPlayersItsNightDoYourAction(){

        sendCommandToOnlineAndSpectatorPlayers(new Command(CommandTypes.itIsNight , null));

        Command informWithAllOnlinePlayersNames = new Command(CommandTypes.doYourAction , getAlivePlayersUserNames());
        Command informWithBadGuyPlayersNames = new Command(CommandTypes.doYourAction  , getAliveBadGuysNames());

        sendCommandToEveryOneExceptAliveMayorAndLector(informWithAllOnlinePlayersNames);

        ServerSidePlayerDetails doctorLectorPlayer = findSpecificAliveRolePlayer(RoleNames.doctorLector);

        if (doctorLectorPlayer != null){
            try {
                doctorLectorPlayer.sendCommandToPlayer(informWithBadGuyPlayersNames);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendCommandToEveryOneExceptAliveMayorAndLector(Command command){

        sendCommandToSpectators(command);
        Iterator<ServerSidePlayerDetails> alivePlayersIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        while (alivePlayersIterator.hasNext()){
            player = alivePlayersIterator.next();

            if(player.getRoleName() != RoleNames.mayor && player.getRoleName() != RoleNames.doctorLector){
                try {
                    player.sendCommandToPlayer(command);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void runTheNightHandlers(){

        ExecutorService executorService = Executors.newCachedThreadPool();
        Iterator<ServerSidePlayerDetails> iterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;

        while (iterator.hasNext()){
            player = iterator.next();
            executorService.execute(new RunnableNightPlayerHandler(player));
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(1 , TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void killThoseWhoDiedTonight(){

        ArrayList<ServerSidePlayerDetails> playersWhoDieTonight = nightEvents.getThoseWhoDieTonight();
        Iterator<ServerSidePlayerDetails> playersWhoDieTonightIterator = playersWhoDieTonight.iterator();
        ServerSidePlayerDetails player = null;
        while (playersWhoDieTonightIterator.hasNext()){
            player = playersWhoDieTonightIterator.next();
            if(player != null){
                if(player.getRoleName() == RoleNames.toughGuy && !toughGuyIsHurt){
                    toughGuyIsHurt = true;
                }
                else {

                    sendCommandToOnlineAndSpectatorPlayers(new Command(CommandTypes.serverToClientString ,
                            "player " + player.getUserName() + " died tonight"));

                    if(player.getRoleName() == RoleNames.professional && nightEvents.mustProfessionalDieForWrongShoot()){
                        try {
                            player.sendCommandToPlayer(new Command(CommandTypes.youAreDead , "you had wrong shoot"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(RoleNames.isEvil(player.getRoleName())) {

                        try {
                            player.sendCommandToPlayer(new Command(CommandTypes.youAreDead , "professional shooted you"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        try {
                            player.sendCommandToPlayer(new Command(CommandTypes.youAreDead , "mafia killed you"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    removePlayerFromOnlinePlayersToSpectators(player);
                }
            }
        }

    }

    private void revealTheDetectedPlayerForDetective(){
        ServerSidePlayerDetails detective = findSpecificAliveRolePlayer(RoleNames.detective);

        if(detective != null){
            ServerSidePlayerDetails detectedPlayer = nightEvents.getWhoDetectiveWantsToDetect();
            if(detectedPlayer != null){
                try {
                    detective.sendCommandToPlayer(new Command(CommandTypes.serverToClientString,
                            "player : " + RoleNames.getRoleAsString(detectedPlayer.getRoleName())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void revealDeadRoles(){
        StringBuilder revealStringBuilder = new StringBuilder("the tough guy asked to reveal the dead roles : \n");
        int counter = 0;
        boolean addedAtLeastOne = false;
        RoleNames[] roleNamesArr = RoleNames.values();
        for(RoleNames roleName : roleNamesArr){
            counter = howManyDeadOnesByRoleName(roleName);
            if(counter > 0){
                addedAtLeastOne = true;
                revealStringBuilder.append(RoleNames.getRoleAsString(roleName)).append(" : ").append(counter).append("\n");
            }
        }

        if(! addedAtLeastOne){
            revealStringBuilder.append("no one has died yet\n");
        }

        Command deadRoleRevealingCommand = new Command(CommandTypes.serverToClientString , revealStringBuilder.toString());
        sendCommandToOnlineAndSpectatorPlayers(deadRoleRevealingCommand);

    }
    private int howManyDeadOnesByRoleName(RoleNames roleName){
        Iterator<ServerSidePlayerDetails> iterator = offlineDeadOnes.iterator();
        ServerSidePlayerDetails playerDetails = null;
        int counter = 0;
        while (iterator.hasNext()){
            playerDetails = iterator.next();
            if(playerDetails.getRoleName() == roleName){
                counter ++;
            }
        }

        iterator = spectators.iterator();
        while (iterator.hasNext()){
            playerDetails = iterator.next();
            if(playerDetails.getRoleName() == roleName){
                counter ++;
            }
        }

        return counter;
    }

}
