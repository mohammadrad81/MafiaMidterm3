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

import javax.swing.plaf.BorderUIResource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * the class for mafia game logic
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class ServerMafiaGameLogic implements ServerSideGame {
    private ArrayList<ServerSidePlayerDetails> alivePlayers;
    private ArrayList<ServerSidePlayerDetails> spectators;
    private ArrayList<ServerSidePlayerDetails> offlineDeadOnes;
    private VotingBox votingBox;
    private NightEvents nightEvents;
    private boolean toughGuyIsHurt;
    boolean isInMiddleOfGame;

    public ServerMafiaGameLogic(ArrayList<ServerSidePlayerDetails> alivePlayers){
        this.spectators = new ArrayList<>();
        this.offlineDeadOnes = new ArrayList<>();
        this.alivePlayers = alivePlayers;
        this.votingBox = new VotingBox();
        this.nightEvents = new NightEvents();
    }

    /**
     * starts the mafia game
     */
    @Override
    public void startTheGame(){

        gameBegins();

        gameEnding();

    }

    /**
     * the beginning of the game
     */
    private void gameBegins(){
        boolean playersHaveBeenIntroduced = false;

        initUserNames();

        waitForAllToGetReady();

        givePlayersTheirRoles();

        isInMiddleOfGame = true;

        while (! isGameOver()){
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

    /**
     * the end of the game
     */
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

        endOfTheGame += "LOSERS : \n";

        for(ServerSidePlayerDetails player : losersArrayList){
            endOfTheGame += player.getUserName() + " was : " + RoleNames.getRoleAsString(player.getRoleName()) + "\n";
        }

        sendCommandToAliveAndSpectatorPlayers(new Command(CommandTypes.endOfTheGame , endOfTheGame));

    }

    /**
     * initials the players' usernames
     */
    private void initUserNames(){
        Command determineYourUserName = new Command(CommandTypes.determineYourUserName , null);

        sendCommandToAlivePlayers(determineYourUserName);

        ExecutorService executorService = Executors.newCachedThreadPool();

        ServerSidePlayerDetails player = null;

        Iterator<ServerSidePlayerDetails> playerIterator = alivePlayers.iterator();

        while (playerIterator.hasNext()){
            player = playerIterator.next();
            executorService.execute(new Thread(new RunnableUserNameTaker(this ,player)));
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1 , TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * gives the players their roles
     */
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

                    System.out.println(player.getUserName() + " role is : " + RoleNames.getRoleAsString(player.getRoleName()));

                } catch (IOException e) {
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }

            else if(counter == 2){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.townDoctor);
                    player.setRoleName(RoleNames.townDoctor);

                    System.out.println(player.getUserName() + " role is : " + RoleNames.getRoleAsString(player.getRoleName()));

                } catch (IOException e) {
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }

            else if(counter == 3){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.detective);
                    player.setRoleName(RoleNames.detective);
                    System.out.println(player.getUserName() + " role is : " + RoleNames.getRoleAsString(player.getRoleName()));

                } catch (IOException e) {
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }

            else if(counter == 4){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.professional);
                    player.setRoleName(RoleNames.professional);
                    System.out.println(player.getUserName() + " role is : " + RoleNames.getRoleAsString(player.getRoleName()));

                } catch (IOException e) {
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }
            else if(counter == 5){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.therapist);
                    player.setRoleName(RoleNames.therapist);
                    System.out.println(player.getUserName() + " role is : " + RoleNames.getRoleAsString(player.getRoleName()));

                } catch (IOException e) {
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }
            else if(counter == 6){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.doctorLector);
                    player.setRoleName(RoleNames.doctorLector);
                    System.out.println(player.getUserName() + " role is : " + RoleNames.getRoleAsString(player.getRoleName()));

                } catch (IOException e) {
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                   continue;
                }
            }
            else if(counter == 7){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.mayor);
                    player.setRoleName(RoleNames.mayor);
                    System.out.println(player.getUserName() + " role is : " + RoleNames.getRoleAsString(player.getRoleName()));

                } catch (IOException e) {
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }
            else if(counter == 8){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.toughGuy);
                    player.setRoleName(RoleNames.toughGuy);
                    System.out.println(player.getUserName() + " role is : " + RoleNames.getRoleAsString(player.getRoleName()));

                } catch (IOException e) {
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }
            else if(counter %3 == 0){
                try {
                    player.sendTheRoleToThePlayer(RoleNames.mafia);
                    player.setRoleName(RoleNames.mafia);
                    System.out.println(player.getUserName() + " role is : " + RoleNames.getRoleAsString(player.getRoleName()));

                } catch (IOException e) {
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }
            else {
                try {
                    player.sendTheRoleToThePlayer(RoleNames.normalCitizen);
                    player.setRoleName(RoleNames.normalCitizen);
                    System.out.println(player.getUserName() + " role is : " + RoleNames.getRoleAsString(player.getRoleName()));
                } catch (IOException e) {
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(player);
                    continue;
                }
            }
            counter ++;
        }
        shuffleThePlayers();
    }

    /**
     * shuffles the players in their list ( to give them their roles randomly )
     */
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

    /**
     * gets a command and do what it says
     * @param command is the receiving command
     */
    public void doTheCommand(Command command){
        syncDoTheCommand(command);
    }

    /**
     * gets a command and do what it says synchronized
     * @param command is the receiving command
     */
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
                if(playerAction.getNameOfThePlayerActionHappensTo() == null){
                    System.out.println("mafia member " +
                            playerAction.getActionDoerName() +
                            " thinks mafia better dont kill anyone tonight .");
                }
                else if(playerAction.getNameOfThePlayerActionHappensTo() != null){

                    System.out.println("mafia member " + playerAction.getActionDoerName() +
                            " says we better kill " +
                            playerAction.getNameOfThePlayerActionHappensTo());

                }

                notifyAliveBadGuysAMafiaMemberChoice(playerAction);
                synchronized (nightEvents){
                    nightEvents.mafiaTakesVictim(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()),
                            false);
                }
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.godFatherVictim){
                if(playerAction.getNameOfThePlayerActionHappensTo() == null){
                    System.out.println("godfather says we kill nobody tonight .");
                }
                else{
                    System.out.println("godfather says we kill " +
                            playerAction.getNameOfThePlayerActionHappensTo() +
                            " tonight .");
                }
                notifyAliveBadGuysTheGodfatherChoice(playerAction.getNameOfThePlayerActionHappensTo());
                synchronized (nightEvents){
                    nightEvents.mafiaTakesVictim(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()),
                            true);
                }
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.townDoctorSave){
                if(playerAction.getNameOfThePlayerActionHappensTo() == null){
                    System.out.println("town doctor saves nobody tonight .");
                }
                else{
                    System.out.println("town doctor saves player : " +
                            playerAction.getNameOfThePlayerActionHappensTo() +
                            " tonight .");
                    synchronized (nightEvents){
                        nightEvents.townDoctorSave(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
                    }
                }
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.doctorLectorSave){
                if(playerAction.getNameOfThePlayerActionHappensTo() == null){
                    System.out.println("doctor lector saves nobody tonight .");
                }
                else {
                    System.out.println("doctor lector saves player : " +
                            playerAction.getNameOfThePlayerActionHappensTo() +
                            " tonight .");
                }
                notifyAliveBadGuysTheDoctorLectorChoice(playerAction.getNameOfThePlayerActionHappensTo());
                nightEvents.lectorSave(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.toughGuySaysShowDeadRoles){
                System.out.println("tough guy says the dead roles should be revealed .");
                synchronized (nightEvents){
                    nightEvents.toughGuySaysShowDeadRoles();
                }
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.detect) {
                if(playerAction.getNameOfThePlayerActionHappensTo() == null){
                    System.out.println("the detective wants to detect nobody .");
                }
                else {
                    System.out.println("the detective wants to detect player : " +
                            playerAction.getNameOfThePlayerActionHappensTo());
                    synchronized (nightEvents){
                        nightEvents.setWhoDetectiveWantsToDetect(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
                    }
                }

            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.mute){
                if(playerAction.getNameOfThePlayerActionHappensTo() == null){
                    System.out.println("the therapist mutes nobody .");
                }
                else {
                    System.out.println("therapist mutes player : " +playerAction.getNameOfThePlayerActionHappensTo());
                    synchronized (nightEvents){
                        nightEvents.mute(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
                    }
                }
            }
            else if(playerAction.getPlayerActionType() == PlayersActionTypes.professionalShoots){
                if(playerAction.getNameOfThePlayerActionHappensTo() == null){
                    System.out.println("the professional shoots nobody .");
                }
                else {
                    System.out.println("the professional shoots player : " + playerAction.getNameOfThePlayerActionHappensTo());
                    synchronized (nightEvents){
                        nightEvents.professionalShoots(getPlayerByName(playerAction.getNameOfThePlayerActionHappensTo()));
                    }
                }
            }
        }
        // other things :
    }

    /**
     * a person who votes , this method notifies the other players this vote
     * @param vote is the vote , the other players are going to know about
     */
    private void notifyOthersThisVote(Vote vote){
        String  voteDescription = null;
        if(vote.getSuspectName() == null){
            voteDescription = "voting : " + vote.getVoterName() + " voted nobody" ;
        }
        else {
            voteDescription = "voting : " + vote.getVoterName() + " voted player : " + vote.getSuspectName();
        }

        Command someOneVoted = new Command(CommandTypes.serverToClientString , voteDescription);
        sendCommandToAliveAndSpectatorPlayers(someOneVoted);

    }

    /**
     * sets a userName for a player , if the username is repetitious , throws exception
     * @param playerDetails is the ServerSidePlayerDetails of the player , we gonna set his/her username
     * @param userName is the username , the player entered
     * @throws RepetitiousUserNameException the exception thrown the moment , the username is repetitious
     */
    public synchronized void setUserName(ServerSidePlayerDetails playerDetails , String userName) throws RepetitiousUserNameException {
        if(isUserNameRepetitious(userName)){
            throw new RepetitiousUserNameException("this userName is already taken");
        }
        else {
            playerDetails.setUserName(userName);
        }
    }

    /**
     * checks if the username is repetitious or not
     * @param userName is the username is checked to be repetitious or not
     * @return true if another player has chosen this username , else false
     */
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

    /**
     * the introduction night of the game
     */
    private void introductionNight(){

        mafiaIntroduction();

        townDoctorAndMayorIntroduction();

    }

    /**
     * mafia members will know each other
     */
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
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(serverSidePlayerDetails);
                }
            }

        }



    }
    /**
     * the doctor will be introduced to the mayor
     */
    private void townDoctorAndMayorIntroduction(){

        ServerSidePlayerDetails mayor = findSpecificAliveRolePlayer(RoleNames.mayor);

        if(mayor != null){
            try {
                mayor.sendCommandToPlayer(new Command(CommandTypes.serverToClientString , getDoctorToMayorIntroduction()));
            } catch (IOException e) {
                removeOfflinePlayerNotifyOthers(mayor);
            }
        }

    }

    /**
     *
     * @return the string that contains the mafia members and their roles
     */
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
        mafiaIntroduction += "mafia : \n";

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

    /**
     * finds a player with a specific RoleName (there is at most one player alive with that role )
     * @param roleName is the roleName of the player we want to find
     * @return the alive player with that roleName , if there is none , null
     */
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

    /**
     * finds a player if by roleName , no matter he/she is alive or not
     * @param roleName is the roleName we want to find the player that has that role
     * @return the player with that role , if there is none , null
     */
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

    /**
     * finds the players that have the input roleName
     * @param roleName is the input roleName
     * @return an arrayList contains the ServerSidePlayerDetails of the players that have the input roleName
     * ( this arrayList might be empty
     */
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

    /**
     *
     * @return a string that introduces the mayor to doctor introduction
     */
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

    /**
     *
     * @return a string that introduces the doctor to mayor
     */
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

    /**
     * removes the player that is no longer online ( connected to the server )
     * and notifies other players that he is not online any more
     * if by the player's absence , the game is over , the game ends
     * @param removingPlayer is the player is removed
     */
    public synchronized void removeOfflinePlayerNotifyOthers(ServerSidePlayerDetails removingPlayer){
        System.err.println("! player " + removingPlayer.getUserName() + " is disconnected !") ;

        if(alivePlayers.contains(removingPlayer)){
            offlineDeadOnes.add(removingPlayer);
            alivePlayers.remove(removingPlayer);
        }

        notifyOthersThePlayerGotOffline(removingPlayer);

        if(isGameOver() && isInMiddleOfGame){
            gameEnding();
        }


    }

    /**
     * notifies the other players ( alive ones and spectators ), the input player is offline
     * @param offlinePlayer is the player that is offline
     */
    private void notifyOthersThePlayerGotOffline(ServerSidePlayerDetails offlinePlayer){

        Iterator<ServerSidePlayerDetails> iterator = alivePlayers.iterator();

        while (iterator.hasNext()){
            ServerSidePlayerDetails messageReceiver = iterator.next();
            try {
                messageReceiver.sendCommandToPlayer(new Command(CommandTypes.serverToClientString ,
                                        " the player : " + offlinePlayer.getUserName()  + " left the game ! "));
            } catch (IOException e) {
//                e.printStackTrace();
                iterator.remove();
                removeOfflinePlayerNotifyOthers(messageReceiver); // if the message receiver is offline too
            }
        }

        iterator = spectators.iterator();

        while (iterator.hasNext()){
            ServerSidePlayerDetails messageReceiver = iterator.next();
            try {
                messageReceiver.sendCommandToPlayer(new Command(CommandTypes.serverToClientString ,
                        " the player : " + offlinePlayer.getUserName()  + " left the game ! "));
            } catch (IOException e) {
//                e.printStackTrace();
                iterator.remove();
                removeSpectatorToOfflineDeadOnes(messageReceiver);
            }
        }

    }

    /**
     * checks if the game is over or not
     * @return true if it is over , else false
     */
    private boolean isGameOver(){

        if(getAliveBadGuysNumber() >= getAliveGoodGuysNumber()){
            return true;
        }

        else if(getAliveBadGuysNumber() == 0){
            return true;
        }

        return false;

    }

    /**
     *
     * @return true if the town wins the game ( all mafias are dead ), else false
     */
    private boolean didTownWinTheGame(){

        if(getAliveBadGuysNumber() == 0){
            return true;
        }

        return false;

    }

    /**
     *
     * @return the number of alive mafia members
     */
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

    /**
     *
     * @return the number of alive town members
     */
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

    /**
     * waits for players to get ready
     */
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

    /**
     * the night of the game ( mafia kills , doctors save and ...)
     */
    private void night(){
        System.out.println("! NIGHT !");
        sendCommandToAliveAndSpectatorPlayers(new Command(CommandTypes.serverToClientString , "! NIGHT !"));
        if(nightEvents.getMutedOne() != null){
            nightEvents.getMutedOne().setMuted(false);
        }

        nightEvents.resetNightEvents();

        informAllPlayersItsNightDoYourAction();

        runTheNightHandlers();

        revealTheDetectedPlayerForDetective();

        killThoseWhoDiedTonight();

        if(nightEvents.mustProfessionalDieForWrongShoot()){
            killProfessionalForWrongShoot();
        }

        if(nightEvents.isShowDeadRoles()) {
            revealDeadRoles();
        }

        if(nightEvents.getMutedOne() != null){
            muteTheMutedOne(nightEvents.getMutedOne());
        }
    }

    /**
     * the day ( players chat )
     */
    private void day(){
        System.out.println("! DAY !");
        sendCommandToAliveAndSpectatorPlayers(new Command(CommandTypes.serverToClientString , "! DAY !"));
        informAllWhoAreAlive();
        tellEveryOneItsChattingTime();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        while (playerDetailsIterator.hasNext()){
            player = playerDetailsIterator.next();
            if(! player.isMuted()){
                executorService.execute(new RunnableServerSideMessageReceiver(player));
            }
        }
        executorService.shutdown();

        try {
            executorService.awaitTermination(6, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tellEveryOneChatIsOver();
    }

    /**
     * informs all players , it is chatting time
     */
    private void tellEveryOneItsChattingTime(){
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        Command chattingTimeCommand = new Command(CommandTypes.chatRoomStarted,  null);
        while (playerDetailsIterator.hasNext()){
            player = playerDetailsIterator.next();
            try {
                player.sendCommandToPlayer(chattingTimeCommand);
            } catch (IOException e) {
                playerDetailsIterator.remove();
                removeOfflinePlayerNotifyOthers(player);
//                e.printStackTrace();
            }
        }

        Iterator<ServerSidePlayerDetails> spectatorsIterator = spectators.iterator();
        ServerSidePlayerDetails spectator = null;
        while (spectatorsIterator.hasNext()){
            spectator = spectatorsIterator.next();

            try {
                spectator.sendCommandToPlayer(chattingTimeCommand);
            } catch (IOException e) {
                spectatorsIterator.remove();
                removeSpectatorToOfflineDeadOnes(spectator);
//                e.printStackTrace();
            }

        }
    }

    /**
     * tells all players that the chat is over
     */
    private void tellEveryOneChatIsOver(){
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        Command chattingTimeCommand = new Command(CommandTypes.chatRoomIsClosed,  null);

        while (playerDetailsIterator.hasNext()){
            player = playerDetailsIterator.next();
            try {
                player.sendCommandToPlayer(chattingTimeCommand);
            } catch (IOException e) {
//                e.printStackTrace();
                removeOfflinePlayerNotifyOthers(player);
            }

        }

        Iterator<ServerSidePlayerDetails> spectatorsIterator = spectators.iterator();
        ServerSidePlayerDetails spectator = null;
        while (spectatorsIterator.hasNext()){
            spectator = spectatorsIterator.next();

            try {
                spectator.sendCommandToPlayer(chattingTimeCommand);
            } catch (IOException e) {
                spectatorsIterator.remove();
                removeSpectatorToOfflineDeadOnes(spectator);
//                e.printStackTrace();
            }
        }
    }

    /**
     * players vote to kill somebody
     */
    private void voting(){
        System.out.println("! VOTING !");
        sendCommandToAliveAndSpectatorPlayers(new Command(CommandTypes.serverToClientString , "! VOTING !"));
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

        if(votingBox.getTheLynchingPlayer() == null){
            votingResult = new Command(CommandTypes.votingResult , " no body is lynched today .");

            sendCommandToAliveAndSpectatorPlayers(votingResult);

        }

        else {
            // you should ask mayor to cancel the voting or not :
            //re write it
            ServerSidePlayerDetails mostVotedPlayer = votingBox.getTheLynchingPlayer();
            votingResult = new Command(CommandTypes.votingResult ,
                    mostVotedPlayer.getUserName() +
                            " is about to lynch today .");

            sendCommandToAliveAndSpectatorPlayers(votingResult);

            if(mayorSaysToLynchOrNot()){
                System.out.println("the lynch happens .");
                finalResult = new Command(CommandTypes.serverToClientString ,
                        "the player " +
                                mostVotedPlayer.getUserName() +
                        " is lynched today ; his role was : " +
                                RoleNames.getRoleAsString(mostVotedPlayer.getRoleName()));
                sendCommandToAliveAndSpectatorPlayers(finalResult);

                try {
                    mostVotedPlayer.sendCommandToPlayer(new Command(CommandTypes.youAreDead , " you are lynched "));
                } catch (IOException e) {
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(mostVotedPlayer);
                }

                removePlayerFromAlivePlayersToSpectators(mostVotedPlayer);
            }

            else {
                System.out.println("the mayor canceled the lynch .");
                 finalResult = new Command(CommandTypes.serverToClientString , "the mayor canceled the lynch . ");
                 sendCommandToAliveAndSpectatorPlayers(finalResult);
            }
        }
        votingBox.resetTheBox();
    }

    /**
     * informs all players ( alive or spectator ) to vote
     */
    private void informAllPlayersItsVotingTime(){
        Command votingTimeInform = new Command(CommandTypes.vote , getAlivePlayersUserNames());
        sendCommandToAliveAndSpectatorPlayers(votingTimeInform);
    }

    /**
     * asks the mayor to lynch the most voted player or not
     * @return true if mayor says lynch or if there is no alive mayor , else false
     */
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
//                e.printStackTrace();
                removeOfflinePlayerNotifyOthers(mayor);
                return true;
            }

            try {
                mayorRespond = mayor.receivePlayerRespond();

            } catch (IOException e) {
//                e.printStackTrace();
                removeOfflinePlayerNotifyOthers(mayor);
                return true;
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

    /**
     *
     * @return an arrayList of the names of the alive players' usersNames
     */
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

    /**
     *
     * @return an arrayList of the names of alive players that are mafia members
     */
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

    /**
     *
     * @return an arrayList of the names of the players that are town members
     */
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

    /**
     * sends a message to all players (alive or spectator
     * @param message is the message is sent to all players
     */
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
//                e.printStackTrace();
                iterator.remove();
                removeOfflinePlayerNotifyOthers(player);
            }

        }

        //sending message to the spectators ( dead online players ) :

        Iterator<ServerSidePlayerDetails> spectatorsIterator = spectators.iterator();
        while (spectatorsIterator.hasNext()){
            player = spectatorsIterator.next();

            try {
                player.sendCommandToPlayer(command);
            } catch (IOException e) {
//                e.printStackTrace();
                spectatorsIterator.remove();
                removeOfflinePlayerNotifyOthers(player);
            }

        }

    }

    /**
     * finds a player that has the input username
     * @param userName is the username of the player
     * @return the player by this username , if there is none , null
     */
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

    /**
     * sends a command to alive players
     * @param command the command to send to alive players
     */
    private synchronized void sendCommandToAlivePlayers(Command command){
        Iterator<ServerSidePlayerDetails> playerIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;

        while (playerIterator.hasNext()){

            player = playerIterator.next();

            try {
                player.sendCommandToPlayer(command);
            } catch (IOException e) {
//                e.printStackTrace();
                playerIterator.remove();
                removeOfflinePlayerNotifyOthers(player);
            }

        }
    }

    /**
     * sends a command to all online players ( alive or spectator )
     * @param command is the command to send to online players
     */
    private void sendCommandToAliveAndSpectatorPlayers(Command command){
        sendCommandToAlivePlayers(command);
        sendCommandToSpectators(command);
    }

    /**
     *
     * @param command
     */
    private void sendCommandToSpectators(Command command){
        Iterator<ServerSidePlayerDetails> playerIterator = spectators.iterator();
        ServerSidePlayerDetails player = null;
        while (playerIterator.hasNext()){

            player = playerIterator.next();
            try {
                player.sendCommandToPlayer(command);
            } catch (IOException e) {
//                e.printStackTrace();
                playerIterator.remove();
                removeOfflinePlayerNotifyOthers(player);
            }
        }
    }

    /**
     * removes a player from alive players to spectators
     * @param player is the player is removed
     */
    private void removePlayerFromAlivePlayersToSpectators(ServerSidePlayerDetails player){

        synchronized (alivePlayers){
            alivePlayers.remove(player);
        }

        synchronized (spectators){
            spectators.add(player);
        }

    }

    /**
     * informs all players ( except mayor ) it is night , and do your action
     */
    private void informAllPlayersItsNightDoYourAction(){

        sendCommandToAliveAndSpectatorPlayers(new Command(CommandTypes.itIsNight , null));

        Command informWithAllOnlinePlayersNames = new Command(CommandTypes.doYourAction , getAlivePlayersUserNames());
        Command informWithGoodGuyPlayersNames = new Command(CommandTypes.doYourAction , getAliveGoodGuysNames());
        Command informWithBadGuyPlayersNames = new Command(CommandTypes.doYourAction  , getAliveBadGuysNames());

        sendCommandToEveryAliveGoodGuysExceptMayor(informWithAllOnlinePlayersNames);
        sendCommandToEveryAliveBadGuysExceptLector(informWithGoodGuyPlayersNames);
        ServerSidePlayerDetails doctorLectorPlayer = findSpecificAliveRolePlayer(RoleNames.doctorLector);

        if (doctorLectorPlayer != null){
            try {
                doctorLectorPlayer.sendCommandToPlayer(informWithBadGuyPlayersNames);
            } catch (IOException e) {
//                e.printStackTrace();
                removeOfflinePlayerNotifyOthers(doctorLectorPlayer);
            }
        }

    }

    /**
     * sends a command to all alive players except mayor
     * @param command is the command to send
     */
    private void sendCommandToEveryAliveGoodGuysExceptMayor(Command command){

        sendCommandToSpectators(command);
        Iterator<ServerSidePlayerDetails> alivePlayersIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        while (alivePlayersIterator.hasNext()){
            player = alivePlayersIterator.next();

            if(player.getRoleName() != RoleNames.mayor && !RoleNames.isEvil(player.getRoleName())){
                try {
                    player.sendCommandToPlayer(command);
                } catch (IOException e) {
//                    e.printStackTrace();
                    alivePlayersIterator.remove();
                    removePlayerFromAlivePlayersToSpectators(player);
                }
            }
        }
    }

    /**
     * sends a command to all alive bad guys except doctor lector
     * @param command is the command to send
     */
    private void sendCommandToEveryAliveBadGuysExceptLector(Command command){
        Iterator<ServerSidePlayerDetails> alivePlayersIterator = alivePlayers.iterator();
        ServerSidePlayerDetails player = null;
        while (alivePlayersIterator.hasNext()){
            player = alivePlayersIterator.next();

            if(player.getRoleName() != RoleNames.doctorLector && RoleNames.isEvil(player.getRoleName())){
                try {
                    player.sendCommandToPlayer(command);
                } catch (IOException e) {
//                    e.printStackTrace();
                    alivePlayersIterator.remove();
                    removeOfflinePlayerNotifyOthers(player);
                }
            }
        }
    }

    /**
     * runs the RunnableNightPlayerHandler for those players that have actions to do at night
     */
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

    /**
     * kills those players that mafia and professional shot at and are not saved
     */
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

                    sendCommandToAliveAndSpectatorPlayers(new Command(CommandTypes.serverToClientString ,
                            "ATTENTION : ! player " + player.getUserName() + " died last night !"));

//                    if(player.getRoleName() == RoleNames.professional && nightEvents.mustProfessionalDieForWrongShoot()){
//                        try {
//                            player.sendCommandToPlayer(new Command(CommandTypes.youAreDead , "you had wrong shoot"));
//                        } catch (IOException e) {
////                            e.printStackTrace();
//                            removeOfflinePlayerNotifyOthers(player);
//                        }
//                    }
                    if(RoleNames.isEvil(player.getRoleName())) {

                        try {
                            player.sendCommandToPlayer(new Command(CommandTypes.youAreDead , "professional shot you"));
                        } catch (IOException e) {
//                            e.printStackTrace();
                            removePlayerFromAlivePlayersToSpectators(player);
                        }

                    }
                    else {
                        try {
                            player.sendCommandToPlayer(new Command(CommandTypes.youAreDead , "mafia killed you"));
                        } catch (IOException e) {
//                            e.printStackTrace();
                            removeOfflinePlayerNotifyOthers(player);
                        }
                    }
                    removePlayerFromAlivePlayersToSpectators(player);
                }
            }
        }

    }

    /**
     * reveals the detected player for the detective
     */
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
//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(detectedPlayer);
                }
            }
        }
    }

    /**
     * reveals the dead roles for online players ( alive or spectator )
     * ( it happens if the tough guy requests it )
     */
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
        sendCommandToAliveAndSpectatorPlayers(deadRoleRevealingCommand);

    }

    /**
     *
     * @param roleName is the roleName , we want to know how many dead ones were
     * @return the number of those players that have the input roleName
     */
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

    /**
     * sends a command to all alive bad guys
     * @param command is the command is about to be sent to alive bad guys
     */
    private void sendCommandToAliveBadGuys(Command command){
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
        ServerSidePlayerDetails playerDetails = null;
        while (playerDetailsIterator.hasNext()){
            playerDetails = playerDetailsIterator.next();
            if(RoleNames.isEvil(playerDetails.getRoleName())){
                try {
                    playerDetails.sendCommandToPlayer(command);
                } catch (IOException e) {

//                    e.printStackTrace();
                    removeOfflinePlayerNotifyOthers(playerDetails);
                }

            }
        }
    }

    /**
     * notifies the alive bad guys who the doctor lector saved
     * @param doctorLectorChoice is the name of the player , the doctor lector saved
     */
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

    /**
     * notifies the alive players , who a mafia member choose
     * @param mafiaChoice is the choice of the mafia member
     */
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

    /**
     * notifies alive bad guys who the doctor lector have chosen
     * @param godfatherChoice is the choice of the godfather
     */
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

    /**
     * @return an arrayList of ServerSidePlayerDetails of good guys ( no matter dead or alive )
     */
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

    /**
     *
     * @return an arrayList of ServerSidePlayerDetails of bad guys ( no matter dead or alive )
     */
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

    /**
     *
     * @return true if godfather is alive , else false
     */
    private boolean isGodfatherAlive(){
        if(findSpecificAliveRolePlayer(RoleNames.godFather) == null){
           return false;
       }
        return true;
    }

    /**
     * makes another player the godfather ( when godfather is dead )
     */
    private void godfatherSubstitution(){
        ArrayList<ServerSidePlayerDetails> aliveBadGuys = getAliveBadGuys();
        Command youAreGodfatherCommand = new Command(CommandTypes.takeYourRole , RoleNames.godFather);
        ServerSidePlayerDetails player = null;
        if(aliveBadGuys.size() == 1){

            try {
                aliveBadGuys.get(0).sendCommandToPlayer(youAreGodfatherCommand);
                aliveBadGuys.get(0).setRoleName(RoleNames.godFather);
                System.out.println("GODFATHER SUBSTITUTE : " + aliveBadGuys.get(0).getUserName());
            } catch (IOException e) {
//                e.printStackTrace();
                removeOfflinePlayerNotifyOthers(aliveBadGuys.get(0));
            }
        }

        else {
            Iterator<ServerSidePlayerDetails> aliveBadGuysIterator = aliveBadGuys.iterator();
            while (aliveBadGuysIterator.hasNext()){
                player = aliveBadGuysIterator.next();
                if(player.getRoleName() != RoleNames.doctorLector){
                    try {
                        player.sendCommandToPlayer(youAreGodfatherCommand);
                        System.out.println("GODFATHER SUBSTITUTE : " + player.getUserName());
                        player.setRoleName(RoleNames.godFather);
                        break;
                    } catch (IOException e) {
                        removeOfflinePlayerNotifyOthers(player);
                        godfatherSubstitution();
                        return;
                    }
                }
            }
        }
    }

    /**
     *
     * @return an arrayList of ServerSidePlayerDetails of bad guy alive players
     */
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

    /**
     * mutes a player ( the therapist choice )
     * @param mutedPlayer the player who gets muted for tomorrow
     */
    private void muteTheMutedOne(ServerSidePlayerDetails mutedPlayer){
        sendCommandToAliveAndSpectatorPlayers(new Command(CommandTypes.serverToClientString ,
                "ATTENTION : ! PLAYER : " + mutedPlayer.getUserName() + " IS MUTED TODAY !"));
        mutedPlayer.setMuted(true);
        Command muteCommand = new Command(CommandTypes.youAreMutedForTomorrow , null);
        try {
            mutedPlayer.sendCommandToPlayer(muteCommand);
        } catch (IOException e) {
//            e.printStackTrace();
            removeOfflinePlayerNotifyOthers(mutedPlayer);
        }
    }

    /**
     * if the professional shoots the wrong person , the professional dies instead
     */
    private void killProfessionalForWrongShoot(){
        Command dieCommand = new Command(CommandTypes.youAreDead ,
                "! you did wrong shoot last night !");
        ServerSidePlayerDetails professional = findSpecificAliveRolePlayer(RoleNames.professional);
        if(professional != null){
            try {
                professional.sendCommandToPlayer(dieCommand);
                sendCommandToAliveAndSpectatorPlayers(new Command(CommandTypes.serverToClientString ,
                        "ATTENTION : ! player " + professional.getUserName() + " died last night !"));
            } catch (IOException e) {
//                e.printStackTrace();
                removeOfflinePlayerNotifyOthers(professional);
            }
        }
    }

    /**
     * removes a spectator to offline dead ones
     * @param player the spectator who
     */
    private void removeSpectatorToOfflineDeadOnes(ServerSidePlayerDetails player){
        System.out.println("player : " + player.getUserName() + " is no longer spectating the game .");
        spectators.remove(player);
        offlineDeadOnes.add(player);
    }

    /**
     * @return a string which contains the names of the alive players
     */
    private String getAlivePlayersNamesAsString(){
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = alivePlayers.iterator();
        StringBuilder alivePlayersNamesAsString = new StringBuilder();
        int counter = 1;
        while (playerDetailsIterator.hasNext()){
            alivePlayersNamesAsString.append(counter+ "- " + playerDetailsIterator.next().getUserName() + "\n");
            counter++;
        }
        return alivePlayersNamesAsString.toString();
    }

    /**
     * sends a string , contains the names of alive players in the game
     * to all alive and spectators
     */
    private void informAllWhoAreAlive(){
        String alivePlayersNamesString = "ALIVE PLAYERS :\n" + getAlivePlayersNamesAsString();
        Command alivePlayersInformCommand = new Command(CommandTypes.serverToClientString , alivePlayersNamesString);
        sendCommandToAliveAndSpectatorPlayers(alivePlayersInformCommand);
    }
}
