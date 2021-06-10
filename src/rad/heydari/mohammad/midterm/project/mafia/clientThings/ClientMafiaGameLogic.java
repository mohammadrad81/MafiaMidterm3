package rad.heydari.mohammad.midterm.project.mafia.clientThings;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.LoopedTillRightInput;
import rad.heydari.mohammad.midterm.project.mafia.MafiaGameException.NoUserFileUtilException;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables.RunnableActionDoer;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables.RunnableClientMessageSender;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables.RunnableClientVote;
import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.gameThings.ClientSideGame;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Role;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.RoleNames;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.badGuys.DoctorLector;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.badGuys.GodFather;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.badGuys.Mafia;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class ClientMafiaGameLogic implements ClientSideGame {
    private  String userName;

    private  Role role;
    private  boolean isMuted;
    private  boolean isAlive;

    private  LoopedTillRightInput loopedTillRightInput;

    private  ObjectOutputStream objectOutputStream;
    private  ObjectInputStream objectInputStream;
    private  Socket socket;

//    private Scanner scanner;




    public ClientMafiaGameLogic(Socket socket, ObjectInputStream objectInputStream , ObjectOutputStream objectOutputStream){
        this.isAlive = true;
        this.isMuted = false;
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.loopedTillRightInput = new LoopedTillRightInput();
//        this.scanner = new Scanner(System.in);
    }

    public void play(){
        while (true){
            try {
                doTheCommand((Command) objectInputStream.readObject());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doTheCommand(Command command){

//        if(command.getTitle() == CommandTypes.ignore){
//            // ignore :)
//            //just for checking connection
//        }

        if (command.getType() == CommandTypes.determineYourUserName){
            determineUserName();
            loopedTillRightInput.createFileUtils(userName);
        }
        else if(command.getType() == CommandTypes.yourUserNameIsSet ){
            confirmPlayerUsername(command);
        }
        else if(command.getType() == CommandTypes.takeYourRole){
            takeYourRole(command);
        }
        else if(command.getType() == CommandTypes.chatRoomStarted){
            chat();
        }
        else if(command.getType() == CommandTypes.vote){
            vote(command);
        }
        else if(command.getType() == CommandTypes.doYourAction){
            doYourAction(command);
        }
        else if(command.getType() == CommandTypes.serverToClientString){
            printServerToClientString(command);
        }
        else if(command.getType() == CommandTypes.waitingForClientToGetReady){
            getReady();
        }
        else if(command.getType() == CommandTypes.youAreDead){
            die(command);
        }
        else if(command.getType() == CommandTypes.endOfTheGame){
            endOfTheGame(command);
        }
        else if(command.getType() == CommandTypes.youAreMutedForTomorrow){
            isMuted = true;
        }



    }

    public void chat(){

        ExecutorService executorService = null;
        Future<?> future = null;

        if(isAlive){

            if(isMuted){
                System.out.println("you are muted but you can listen what people say : ");
            }

            else {
                System.out.println("good morning , such a good day to chat\n"+
                        "(if you enter \"ready\" you can only receive messages, no more sending ):");

                executorService = Executors.newCachedThreadPool();
                future = executorService.submit(new RunnableClientMessageSender(userName , objectOutputStream));
            }

        }

        else {
            System.out.println("good morning , the alive people are chatting : ");


        }

        Command command = null;
        Message message = null;
        do {
            command =  receiveServerCommand();

            if(command.getType() == CommandTypes.newMessage){
                printMessage(message = (Message) command.getCommandNeededThings());
                try {
                    loopedTillRightInput.saveMessage(message);
                } catch (NoUserFileUtilException e) {
                    e.printStackTrace();
                }
            }

            else if(command.getType() != CommandTypes.chatRoomIsClosed){ // just in case :
                doTheCommand(command);
                break;
            }

        }while (command.getType() != CommandTypes.chatRoomIsClosed);
        System.out.println(" chat room is closed ");

        if(isAlive && !isMuted){
            executorService.shutdown();
            try {
                future.cancel(true);
                executorService.awaitTermination(1  , TimeUnit.MILLISECONDS );

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(isMuted){
            System.out.println("you are not muted any more ");
            isMuted = false;
        }

    }

    public void vote(Command command){
        System.out.println("! VOTING TIME !");
        ExecutorService executorService = null;
        Future<?> future = null;
        Command receivingCommand = null;

        if(isAlive){
            System.out.println(" vote somebody to lynch today : ");
            ArrayList<String> playersNames =(ArrayList<String>) command.getCommandNeededThings();
            playersNames.remove(userName);
             executorService = Executors.newCachedThreadPool();
             future = executorService.submit(new Thread(new RunnableClientVote(userName , playersNames , objectOutputStream)));
            executorService.shutdown();
        }

        else {
            System.out.println("waiting for alive players to vote ...");
        }




        do {
            receivingCommand = receiveServerCommand();

            if(receivingCommand.getType() == CommandTypes.serverToClientString){
                System.out.println(receivingCommand.getCommandNeededThings());

            }

            else if(receivingCommand.getType() == CommandTypes.votingResult) {

                System.out.println("voting time is over");

                if(isAlive){
                    try {

                        future.cancel(true);
                        executorService.awaitTermination(0 , TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                System.out.println(receivingCommand.getCommandNeededThings());//printing voting result
            }

        }while (receivingCommand.getType() != CommandTypes.votingResult);


    }

    public void doYourAction(Command command){
        ExecutorService executorService = Executors.newCachedThreadPool();
        if(isAlive){
            if (role instanceof Actionable){
                executorService.execute(new RunnableActionDoer((Actionable) role , command));
                executorService.shutdown();
            }

            else {
                System.out.println("waiting for other players ...");
            }
        }

        else {
            System.out.println("it is night , waiting for other players to do their actions .");
        }
        // else nothing , the server had handled it before
    }

    public void takeYourRole(Command command){
        this.role =  generateRole((RoleNames) command.getCommandNeededThings() ,
                objectOutputStream ,
                objectInputStream);
        printTheClientsRole();
    }

    public void printTheClientsRole(){
        System.out.println("your role is : " + role.getRoleNameString());
    }

    public void determineUserName(){
        String testingUserName = null;
        Command serverRespond = null;
        System.out.println(" Enter your username\n"+
                "( exit is not valid , if you enter that , you leave the game ) :" );
        while (true){
            try {
                testingUserName = loopedTillRightInput.stringInput().trim();

                if(testingUserName.equals("exit")){
                    playerExitsTheGame();

                }

                objectOutputStream.writeObject(new Command(CommandTypes.setMyUserName , testingUserName ));
                serverRespond = receiveServerCommand();

                if(serverRespond.getType() == CommandTypes.yourUserNameIsSet){
                    doTheCommand(serverRespond);
                    break;
                }

                else if(serverRespond.getType() == CommandTypes.repetitiousUserName){
                    System.out.println("oops ... \n some one else already has chosen that username , try again : ");
                }

            } catch (IOException e) {
                System.out.println("connection lost");
                e.printStackTrace();
                playerExitsTheGame();
            }
        }
    }

    public void playerExitsTheGame(){

//        try {
//            objectOutputStream.writeObject(new Command(CommandTypes.iExitTheGame , null));
//            System.exit(0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        System.out.println("! you exited the game !");
        try {
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.exit(0);
        }
    }

    public void confirmPlayerUsername(Command command){
        this.userName = (String) command.getCommandNeededThings();
        System.out.println("your username : " + this.userName);
    }

//    public void printMessageFromServer(Command command){
//        System.out.println(command.getCommandNeededThings());
//    }

    public Role generateRole(RoleNames roleName , ObjectOutputStream objectOutputStream , ObjectInputStream objectInputStream){
        if(roleName == RoleNames.godFather){
            return new GodFather(objectInputStream , objectOutputStream , userName);
        }
        else if(roleName == RoleNames.doctorLector){
            return new DoctorLector(objectInputStream , objectOutputStream ,userName);
        }
        else if(roleName == RoleNames.detective){
            return new Detective(objectInputStream , objectOutputStream, userName);
        }
        else if(roleName ==  RoleNames.mafia){
            return new Mafia(objectInputStream , objectOutputStream , userName);
        }
        else if(roleName == RoleNames.mayor){
            return new Mayor(objectInputStream , objectOutputStream , userName);
        }
        else if(roleName == RoleNames.normalCitizen){
            return new NormalCitizen(objectInputStream , objectOutputStream);
        }
        else if(roleName == RoleNames.professional){
            return new Professional(objectInputStream , objectOutputStream , userName);
        }
        else if(roleName == RoleNames.therapist){
            return new Therapist(objectInputStream , objectOutputStream , userName);
        }
        else if(roleName == RoleNames.toughGuy){
            return new ToughGuy(objectInputStream , objectOutputStream , userName);
        }
        else if(roleName == RoleNames.townDoctor){
            return new TownDoctor(objectInputStream , objectOutputStream , userName);
        }
        else{
            return null;
        }
    }

    public void printServerToClientString(Command command){
        System.out.println(command.getCommandNeededThings());
    }

    private void getReady(){

        System.out.println("Enter ready to inform the god that you are ready : ");
        String input = "";

        do {
            input = loopedTillRightInput.stringInput().trim();

            if(input.equals("exit")){
                playerExitsTheGame();
            }

            else if(! input.equals("ready")){
                System.out.println("not valid input");
            }
        }while (! input.equals("ready"));

        try {
            objectOutputStream.writeObject(new Command(CommandTypes.imReady , null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Command receiveServerCommand(){

        try {
            return (Command) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendCommandToServer(Command command){

        try {
            objectOutputStream.writeObject(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printMessage(Message message){
        System.out.println(message.getSenderName() + ": "+ message.getMessageText());
    }

    private void die(Command command){
        isAlive = false;
        System.out.println("you are dead");
        System.out.println("reason : " + command.getCommandNeededThings());
    }

    private void endOfTheGame(Command command){
        System.out.println(command.getCommandNeededThings());
        System.exit(0);
    }

}
