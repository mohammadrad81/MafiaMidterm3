package rad.heydari.mohammad.midterm.project.mafia.serverThings;

import rad.heydari.mohammad.midterm.project.mafia.MafiaGameException.RepetitiousUserNameException;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables.RunnableServerSideMessageReceiver;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables.RunnableUserNameTaker;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables.RunnableVoteHandler;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.ServerSideRunnables.RunnableWaitToGetReady;
import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Demand;
import rad.heydari.mohammad.midterm.project.mafia.gameThings.ServerSideGame;
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
    private ArrayList<ServerSidePlayerDetails> onlinePlayers;
    private ArrayList<ServerSidePlayerDetails> spectators;
    private ArrayList<ServerSidePlayerDetails> offlineDeadOnes;
    private VotingBox votingBox;

    public ServerMafiaGameLogic(ArrayList<ServerSidePlayerDetails> onlinePlayers){
        this.spectators = new ArrayList<>();
        this.offlineDeadOnes = new ArrayList<>();
        this.onlinePlayers = onlinePlayers;
        this.votingBox = new VotingBox();
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

        Iterator<ServerSidePlayerDetails> playerIterator = onlinePlayers.iterator();

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

        Iterator<ServerSidePlayerDetails> playerIterator = onlinePlayers.iterator();
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
        while (onlinePlayers.size() > 0){
            randomNumber = random.nextInt(onlinePlayers.size());
            shuffledPlayers.add(onlinePlayers.get(randomNumber));
            onlinePlayers.remove(randomNumber);
        }
        onlinePlayers = shuffledPlayers;

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

        // other things :

    }

    private synchronized void doTheDemand(Demand demand){
        Command command = demand.getCommand();
        ServerSidePlayerDetails demander = demand.getDemander();

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

        for(ServerSidePlayerDetails serverSidePlayerDetails : onlinePlayers){
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

        for(ServerSidePlayerDetails serverSidePlayerDetails : onlinePlayers){
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
        String mayorToDoctorIntroduction;
        ServerSidePlayerDetails doctor = findSpecificRolePlayer(RoleNames.townDoctor);
        ServerSidePlayerDetails mayor = findSpecificRolePlayer(RoleNames.mayor);

        if(doctor != null){
            try {
                doctor.sendCommandToPlayer(new Command(CommandTypes.serverToClientString , getMayorToDoctorIntroduction()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

        if(findSpecificRolePlayer(RoleNames.godFather) != null){
            mafiaIntroduction += findSpecificRolePlayer(RoleNames.godFather).getUserName() + "\n";
        }
        else {
            mafiaIntroduction += "no one\n";
        }

        mafiaIntroduction += "doctor lector : ";

        if(findSpecificRolePlayer(RoleNames.doctorLector) != null){
            mafiaIntroduction += findSpecificRolePlayer(RoleNames.doctorLector).getUserName() + "\n";
        }
        else{
            mafiaIntroduction += "no one\n";
        }
        mafiaIntroduction += "mafia : ";

        if(findSameRolesPlayers(RoleNames.mafia).size() == 0){
            mafiaIntroduction += "no one";
        }
        else {
            for (ServerSidePlayerDetails serverSidePlayerDetails : findSameRolesPlayers(RoleNames.mafia)){

                mafiaIntroduction += serverSidePlayerDetails.getUserName() + "\n";
            }
        }

        return mafiaIntroduction;
    }

    private ServerSidePlayerDetails findSpecificRolePlayer(RoleNames roleName){
        Iterator<ServerSidePlayerDetails> iterator = onlinePlayers.iterator();

        while (iterator.hasNext()){
            ServerSidePlayerDetails player = iterator.next();
            if(player.getRoleName() == roleName){
                return player;
            }
        }
        return null;
    }

    private ArrayList<ServerSidePlayerDetails> findSameRolesPlayers(RoleNames roleName){
        Iterator<ServerSidePlayerDetails> iterator = onlinePlayers.iterator();

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
        ServerSidePlayerDetails mayor = findSpecificRolePlayer(RoleNames.mayor);
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
        ServerSidePlayerDetails doctor = findSpecificRolePlayer(RoleNames.townDoctor);

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


        if(onlinePlayers.contains(removingPlayer)){
            offlineDeadOnes.add(removingPlayer);
            onlinePlayers.remove(removingPlayer);
        }
    }

    private void notifyOthersThePlayerGotOffline(ServerSidePlayerDetails offlinePlayer){

        Iterator<ServerSidePlayerDetails> iterator = onlinePlayers.iterator();

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
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = onlinePlayers.iterator();
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
            Iterator<ServerSidePlayerDetails> playerDetailsIterator = onlinePlayers.iterator();
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

        Iterator<ServerSidePlayerDetails> playerDetailsIterator = onlinePlayers.iterator();

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

    }

    private void day(){
        tellEveryOneItsChattingTime();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = onlinePlayers.iterator();
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
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = onlinePlayers.iterator();
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
        Iterator<ServerSidePlayerDetails> playerDetailsIterator = onlinePlayers.iterator();
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
        Iterator<ServerSidePlayerDetails> onlinePlayersIterator = onlinePlayers.iterator();
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

        Iterator<ServerSidePlayerDetails> iterator = onlinePlayers.iterator();
        ServerSidePlayerDetails player = null;
        ArrayList<String> playersNames = new ArrayList<>();

        while (iterator.hasNext()){
            player = iterator.next();
            playersNames.add(player.getUserName());
        }

        return playersNames;
    }



    public void sendMessageToAll(Message message){
        // sending to alive players :

        Iterator<ServerSidePlayerDetails> iterator = onlinePlayers.iterator();
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
        Iterator<ServerSidePlayerDetails> iterator = onlinePlayers.iterator();
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
        Iterator<ServerSidePlayerDetails> playerIterator = onlinePlayers.iterator();
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

    private synchronized void sendCommandToOnlineAndSpectatorPlayers(Command command){
        sendCommandToOnlinePlayers(command);

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

        synchronized (onlinePlayers){
            onlinePlayers.remove(player);
        }

        synchronized (spectators){
            spectators.add(player);
        }

    }
}
