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


        while (! isGameOver()){ // it should be !isGameOver()

            if(! playersHaveBeenIntroduced){
                introductionNight();
                playersHaveBeenIntroduced = true;
            }

            else {
                night();
            }
            if(!isGodfatherAlive()){
                godfatherSubstitution();
            }

            if(isGameOver()){
                break;
            }

            day();

            voting();

            if( ! isGodfatherAlive()){
                godfatherSubstitution();
            }

        }

        gameEnding();

    }

    private void gameEnding(){
        ArrayList<ServerSidePlayerDetails> winnersArrayList = null;
        ArrayList<ServerSidePlayerDetails> losersArrayList = null;
        String endOfTheGame ="! END OF THE GAME !\nthe winner team : ";
        if(didTownWinTheGame()){

            endOfTheGame += " villagers\n";
            winnersArrayList = getGoodGuysNoMatterDeadOrAlive();
            losersArrayList = getBadGuysNoMatterDeadOrAlive();

        }
        else {

            endOfTheGame += " mafias\n";
            winnersArrayList = getBadGuysNoMatterDeadOrAlive();
            losersArrayList = getGoodGuysNoMatterDeadOrAlive();

        }

        endOfTheGame += "WINNERS : \n";

        for(ServerSidePlayerDetails player : winnersArrayList){
            endOfTheGame += player.getUserName() + " was : " + RoleNames.getRoleAsString(player.getRoleName()) + "\n";
        }

        endOfTheGame += "LOSERS : ";

        for(ServerSidePlayerDetails player : losersArrayList){
            endOfTheGame += player.getUserName() + " was : " + RoleNames.getRoleAsString(player.getRoleName()) + "\n";
        }

        sendCommandToOnlineAndSpectatorPlayers(new Command(CommandTypes.endOfTheGame , endOfTheGame));

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
            if(playerAction.getPlayerActionType() == PlayersActionTypes.mafiaVictim){
                if(playerAction.getNameOfThePlayerActionHappensTo() != null){
                    notifyAliveBadGuysAMafiaMemberChoice(playerAction);
                    synchronized (nightEvents){
                        nightEvents.mafiaTakesVictim(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()),
                                false);
                    }
                }
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.godFatherVictim){
                notifyAliveBadGuysTheGodfatherChoice(playerAction.getNameOfThePlayerActionHappensTo());
                synchronized (nightEvents){
                    nightEvents.mafiaTakesVictim(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()),
                            true);
                }
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.townDoctorSave){
                if(playerAction.getNameOfThePlayerActionHappensTo() != null){
                    synchronized (nightEvents){
                        nightEvents.townDoctorSave(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
                    }
                }
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.doctorLectorSave){
                notifyAliveBadGuysTheDoctorLectorChoice(playerAction.getNameOfThePlayerActionHappensTo());
                nightEvents.lectorSave(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.toughGuySaysShowDeadRoles){
                synchronized (nightEvents){
                    nightEvents.toughGuySaysShowDeadRoles();
                }
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.detect){
                synchronized (nightEvents){
                    nightEvents.setWhoDetectiveWantsToDetect(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
                }
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.mute){
                synchronized (nightEvents){
                    nightEvents.mute(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
                }
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.professionalShoots){
                synchronized (nightEvents){
                    nightEvents.professionalShoots(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
                }
            }
        }
        // other things :
    }

    private void notifyOthersThisVote(Vote vote){
        String  voteDescription = null;
        if(vote.getSuspectName() == null){
            voteDescription = "voting : " + vote.getVoterName() + " voted nobody" ;
        }
        else {
            voteDescription = "voting : " + vote.getVoterName() + " voted player : " + vote.getSuspectName();
        }

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

        if(getAliveBadGuysNumber() >= getAliveGoodGuysNumber()){
            return true;
        }

        else if(getAliveBadGuysNumber() == 0){
            return true;
        }

        return false;

    }

    private boolean didTownWinTheGame(){

        if(getAliveBadGuysNumber() == 0){
            return true;
        }

        return false;

    }

    private int getAliveBadGuysNumber(){
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

    private int getAliveGoodGuysNumber(){
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

        if(nightEvents.isShowDeadRoles()) {
            revealDeadRoles();
        }

        if(nightEvents.getMutedOne() != null){
            muteTheMutedOne(nightEvents.getMutedOne());
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
        Command finalResult;
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
            executorService.awaitTermination(6 , TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(votingBox.getMostVotedPlayer() == null){
            votingResult = new Command(CommandTypes.votingResult , " no body is lynched today");

            sendCommandToOnlineAndSpectatorPlayers(votingResult);

        }

        else {
            // you should ask mayor to cancel the voting or not :
            //re write it
            ServerSidePlayerDetails mostVotedPlayer = votingBox.getMostVotedPlayer();
            votingResult = new Command(CommandTypes.votingResult ,
                    mostVotedPlayer.getUserName() +
                            " is about to lynch today .");

            sendCommandToOnlineAndSpectatorPlayers(votingResult);

            if(mayorSaysToLynchOrNot()){

                finalResult = new Command(CommandTypes.serverToClientString ,
                        "the player " +
                                mostVotedPlayer.getUserName() +
                        " is lynched today ; his role was : " +
                                RoleNames.getRoleAsString(mostVotedPlayer.getRoleName()));
                sendCommandToOnlineAndSpectatorPlayers(finalResult);

                try {
                    mostVotedPlayer.sendCommandToPlayer(new Command(CommandTypes.youAreDead , " you are lynched "));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                removePlayerFromOnlinePlayersToSpectators(mostVotedPlayer);
            }

            else {
                 finalResult = new Command(CommandTypes.serverToClientString , "the mayor canceled the lynch . ");
                 sendCommandToOnlineAndSpectatorPlayers(finalResult);
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

    private boolean mayorSaysToLynchOrNot(){
        ServerSidePlayerDetails mayor = findSpecificAliveRolePlayer(RoleNames.mayor);
        Command mayorRespond = null;

        if(mayor == null){
            return true; //lynch
        }
        else {
            try {
                mayor.sendCommandToPlayer(new Command(CommandTypes.doYourAction , null));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mayorRespond = mayor.receivePlayerRespond();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if(mayorRespond.getType() == CommandTypes.mayorSaysLynch){
                return true;//lynch
            }
            else if(mayorRespond.getType() == CommandTypes.mayorSaysDontLynch){
                return false;//don't lynch
            }
        }

        return false;
    }

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

    private ArrayList<String> getAliveGoodGuysNames(){
        ArrayList<String> aliveGoodGuysNames = new ArrayList<>();
        Iterator<ServerSidePlayerDetails> playerIterator = alivePlayers.iterator();
        ServerSidePlayerDetails playerDetails = null;
        synchronized (alivePlayers){
            while (playerIterator.hasNext()){
                playerDetails = playerIterator.next();
                if(! RoleNames.isEvil(playerDetails.getRoleName())){
                    aliveGoodGuysNames.add(playerDetails.getUserName());
                }
            }
        }
        return aliveGoodGuysNames;
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
        Command informWithGoodGuyPlayersNames = new Command(CommandTypes.doYourAction , getAliveGoodGuysNames());
        Command informWithBadGuyPlayersNames = new Command(CommandTypes.doYourAction  , getAliveBadGuysNames());

        sendCommandToEveryGoodGuysExceptMayor(informWithAllOnlinePlayersNames);
        sendCommandToEveryBadGuysExceptLector(informWithGoodGuyPlayersNames);
        ServerSidePlayerDetails doctorLectorPlayer = findSpecificAliveRolePlayer(RoleNames.doctorLector);

        if (doctorLectorPlayer != null){
            try {
                doctorLectorPlayer.sendCommandToPlayer(informWithBadGuyPlayersNames);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendCommandToEveryGoodGuysExceptMayor(Command command){

        sendCommandToSpectators(command);
        Iterator<ServerSidePlayerDetails> alivePlayersIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        while (alivePlayersIterator.hasNext()){
            player = alivePlayersIterator.next();

            if(player.getRoleName() != RoleNames.mayor && !RoleNames.isEvil(player.getRoleName())){
                try {
                    player.sendCommandToPlayer(command);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendCommandToEveryBadGuysExceptLector(Command command){
        Iterator<ServerSidePlayerDetails> alivePlayersIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        while (alivePlayersIterator.hasNext()){
            player = alivePlayersIterator.next();

            if(player.getRoleName() != RoleNames.doctorLector && RoleNames.isEvil(player.getRoleName())){
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
            if(player.getRoleName() != RoleNames.normalCitizen && player.getRoleName() != RoleNames.mayor){
                executorService.execute(new RunnableNightPlayerHandler(player));
            }
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
            Command detectionResult = null;
            if(detectedPlayer != null){
                if(RoleNames.isEvil(detectedPlayer.getRoleName()) && detectedPlayer.getRoleName() != RoleNames.godFather){
                    detectionResult = new Command(CommandTypes.serverToClientString ,
                            "the player : " +
                                    detectedPlayer.getUserName() +
                            " is evil ( bad guy ).");
                }

                else {
                    detectionResult = new Command(CommandTypes.serverToClientString ,
                            "the player : " +
                                    detectedPlayer.getUserName() +
                            " is not evil ( he is a good guy ) .");
                }

                try {
                    detective.sendCommandToPlayer(detectionResult);
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

    private void sendCommandToAliveBadGuys(Command command){
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
        ServerSidePlayerDetails playerDetails = null;
        while (playerDetailsIterator.hasNext()){
            playerDetails = playerDetailsIterator.next();
            if(RoleNames.isEvil(playerDetails.getRoleName())){
                try {
                    playerDetails.sendCommandToPlayer(command);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void notifyAliveBadGuysTheDoctorLectorChoice(String doctorLectorChoice){
        Command doctorLectorChoiceNotifyCommand = null;
        if(doctorLectorChoice == null){
            doctorLectorChoiceNotifyCommand = new Command(CommandTypes.serverToClientString , "doctor lector saved nobody");

        }
        else {
            doctorLectorChoiceNotifyCommand =
                    new Command(CommandTypes.serverToClientString ,
                            "doctor lector saved the player : " +
                                    doctorLectorChoice);

        }
        sendCommandToAliveBadGuys(doctorLectorChoiceNotifyCommand);
    }

    private void notifyAliveBadGuysAMafiaMemberChoice(PlayerAction mafiaChoice){
        Command mafiaChoiceNotifyCommand = null;

        if(mafiaChoice.getNameOfThePlayerActionHappensTo() == null){
            mafiaChoiceNotifyCommand =
                    new Command(CommandTypes.serverToClientString ,
                    mafiaChoice.getActionDoerName() + " thinks we better don't kill anyone tonight .");
        }
        else {
            mafiaChoiceNotifyCommand =
                    new Command(CommandTypes.serverToClientString ,
                            mafiaChoice.getActionDoerName() + " thinks we better kill " +
                                    mafiaChoice.getNameOfThePlayerActionHappensTo() +
                                    " tonight.");
        }
        sendCommandToAliveBadGuys(mafiaChoiceNotifyCommand);
    }

    private void notifyAliveBadGuysTheGodfatherChoice(String godfatherChoice){
        Command godfatherChoiceNotifyCommand = null;

        if(godfatherChoice == null){
            godfatherChoiceNotifyCommand =
                    new Command(CommandTypes.serverToClientString ,
                    "the great godfather says we don't kill anyone tonight .");
        }
        else {
            godfatherChoiceNotifyCommand =
                    new Command(CommandTypes.serverToClientString ,
                            "the great godfather says we kill " +
                                    godfatherChoice +
                            " tonight .");
        }
        sendCommandToAliveBadGuys(godfatherChoiceNotifyCommand);
    }

    private ArrayList<ServerSidePlayerDetails> getGoodGuysNoMatterDeadOrAlive(){
        ArrayList<ServerSidePlayerDetails> goodGuysArrayList = new ArrayList<>();
        Iterator<ServerSidePlayerDetails> playerIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        while (playerIterator.hasNext()){
            player = playerIterator.next();

            if(! RoleNames.isEvil(player.getRoleName())){
                goodGuysArrayList.add(player);
            }

        }

        playerIterator = spectators.iterator();

        while (playerIterator.hasNext()){
            player = playerIterator.next();
            if(!RoleNames.isEvil(player.getRoleName())){
                goodGuysArrayList.add(player);
            }
        }

        playerIterator = offlineDeadOnes.iterator();

        while (playerIterator.hasNext()){
            player = playerIterator.next();
            if(!RoleNames.isEvil(player.getRoleName())){
                goodGuysArrayList.add(player);
            }
        }

        return goodGuysArrayList;
    }

    private ArrayList<ServerSidePlayerDetails> getBadGuysNoMatterDeadOrAlive(){
        ArrayList<ServerSidePlayerDetails> badGuysArrayList = new ArrayList<>();
        Iterator<ServerSidePlayerDetails> playerIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;

        while (playerIterator.hasNext()){
            player = playerIterator.next();

            if(RoleNames.isEvil(player.getRoleName())){
                badGuysArrayList.add(player);
            }

        }

        playerIterator = spectators.iterator();

        while (playerIterator.hasNext()){
            player = playerIterator.next();
            if(RoleNames.isEvil(player.getRoleName())){
                badGuysArrayList.add(player);
            }
        }

        playerIterator = offlineDeadOnes.iterator();

        while (playerIterator.hasNext()){
            player = playerIterator.next();
            if(RoleNames.isEvil(player.getRoleName())){
                badGuysArrayList.add(player);
            }
        }

        return badGuysArrayList;
    }

    private boolean isGodfatherAlive(){
        if(findSpecificAliveRolePlayer(RoleNames.godFather) == null){
           return false;
       }
        return true;
    }

    private void godfatherSubstitution(){
        ArrayList<ServerSidePlayerDetails> aliveBadGuys = getAliveBadGuys();
        Command youAreGodfatherCommand = new Command(CommandTypes.takeYourRole , RoleNames.godFather);
        ServerSidePlayerDetails player = null;
        if(aliveBadGuys.size() == 1){

            try {
                aliveBadGuys.get(0).sendCommandToPlayer(youAreGodfatherCommand);
                aliveBadGuys.get(0).setRoleName(RoleNames.godFather);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            Iterator<ServerSidePlayerDetails> aliveBadGuysIterator = aliveBadGuys.iterator();
            while (aliveBadGuysIterator.hasNext()){
                player = aliveBadGuysIterator.next();
                if(player.getRoleName() != RoleNames.doctorLector){
                    try {
                        player.sendCommandToPlayer(youAreGodfatherCommand);
                        player.setRoleName(RoleNames.godFather);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private ArrayList<ServerSidePlayerDetails> getAliveBadGuys(){
        ArrayList<ServerSidePlayerDetails> aliveBadGuys = new ArrayList<>();
        Iterator<ServerSidePlayerDetails> playerIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        while (playerIterator.hasNext()){
            player = playerIterator.next();
            if(RoleNames.isEvil(player.getRoleName())){
                aliveBadGuys.add(player);
            }
        }

        return aliveBadGuys;
    }

    private void muteTheMutedOne(ServerSidePlayerDetails mutedPlayer){
        Command muteCommand = new Command(CommandTypes.youAreMutedForTomorrow , null);
        try {
            mutedPlayer.sendCommandToPlayer(muteCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
