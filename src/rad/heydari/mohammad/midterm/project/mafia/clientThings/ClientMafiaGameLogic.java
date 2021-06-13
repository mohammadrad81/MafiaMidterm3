package rad.heydari.mohammad.midterm.project.mafia.clientThings;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.InputThings.LoopedTillRightInput;
import rad.heydari.mohammad.midterm.project.mafia.MafiaGameException.NoUserFileUtilException;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables.RunnableActionDoer;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables.RunnableClientMessageSender;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables.RunnableClientVote;
import rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables.RunnableInputTaker;
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

/**
 * class for the game logic of the mafia game
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @see Command
 * @see CommandTypes
 * @see InputProducer
 * @see LoopedTillRightInput
 * @see rad.heydari.mohammad.midterm.project.mafia.night.PlayerAction
 * @see  rad.heydari.mohammad.midterm.project.mafia.night.PlayersActionTypes
 * @see Role
 * @see RoleNames
 */
public class ClientMafiaGameLogic implements ClientSideGame {
    private  String userName;

    private  Role role;
    private  boolean isMuted;
    private  boolean isAlive;

    private  LoopedTillRightInput loopedTillRightInput;

    private  ObjectOutputStream objectOutputStream;
    private  ObjectInputStream objectInputStream;
    private  Socket socket;
    private RunnableInputTaker runnableInputTaker;
    private InputProducer inputProducer;

//    private Scanner scanner;


    /**
     * constructor for the game
     * @param socket is the connection socket to the server
     * @param objectInputStream is the objectInputStream made by the socket inputStream
     * @param objectOutputStream is the objectOutputStream made by the socket outputStream
     */
    public ClientMafiaGameLogic(Socket socket, ObjectInputStream objectInputStream , ObjectOutputStream objectOutputStream){
        this.isAlive = true;
        this.isMuted = false;
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.loopedTillRightInput = new LoopedTillRightInput();
//        this.scanner = new Scanner(System.in);
        this.inputProducer = new InputProducer(loopedTillRightInput);
        this.runnableInputTaker = new RunnableInputTaker(inputProducer);
    }

    /**
     * start of the game
     */
    public void play(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(runnableInputTaker);
        executorService.shutdown();
        while (true){
            try {
                doTheCommand((Command) objectInputStream.readObject());

            } catch (IOException e) {
                disConnection();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * takes a command and do it
     * @param command is the command from server
     */
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
            synchronized (inputProducer){
                inputProducer.clearInputs();
            }
            chat();
        }
        else if(command.getType() == CommandTypes.vote){
            synchronized (inputProducer){
                inputProducer.clearInputs();
            }
            vote(command);
        }
        else if(command.getType() == CommandTypes.doYourAction){
            synchronized (inputProducer){
                inputProducer.clearInputs();
            }
            doYourAction(command);
        }
        else if(command.getType() == CommandTypes.serverToClientString){
            printServerToClientString(command);
        }
        else if(command.getType() == CommandTypes.waitingForClientToGetReady){
            inputProducer.clearInputs();
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

    /**
     * player chats
     */
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
                future = executorService.submit(new RunnableClientMessageSender(userName , objectOutputStream , inputProducer));
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

    /**
     * player votes
     * @param command is the command contains the name of other players
     */
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
             future = executorService.submit(new Thread(new RunnableClientVote(userName , playersNames , objectOutputStream , inputProducer)));
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

    /**
     * player does his action
     * @param command is the command contains the needed things for the player action
     */
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

    /**
     * player takes his RoleName and creates his role by the roleFactory
     * @param command is the command contains the roleName from server
     */
    public void takeYourRole(Command command){
        this.role =  generateRole((RoleNames) command.getCommandNeededThings() ,
                objectOutputStream ,
                objectInputStream);
        printTheClientsRole();
    }

    /**
     * prints the role of the player
     */
    public void printTheClientsRole(){
        System.out.println("your role is : " + role.getRoleString());
    }

    /**
     * player write his username and if it is not repetitious , it will be set , else , player enters another
     */
    public void determineUserName(){
        String testingUserName = null;
        Command serverRespond = null;
        System.out.println(" Enter your username\n"+
                "( exit is not valid , if you enter that , you leave the game ) :" );
        while (true){
            try {

                while (!inputProducer.hasNext()){
                    Thread.sleep(200);
                }

                testingUserName = inputProducer.consumeInput().trim();

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
                disConnection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * player exits the game , by closing the client program
     */
    public void playerExitsTheGame(){

        System.out.println("! you exited the game !");
        try {
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();

        } catch (IOException e) {
            disConnection();
        }finally {
            System.exit(0);
        }
    }

    /**
     * the entered username of the player is set ( it was not repetitious )
     * @param command is the command from server contains the username
     */
    public void confirmPlayerUsername(Command command){
        this.userName = (String) command.getCommandNeededThings();
        System.out.println("your username : " + this.userName);
    }

//    public void printMessageFromServer(Command command){
//        System.out.println(command.getCommandNeededThings());
//    }

    /**
     * a role factory that makes a role by input roleName and streams
     * @param roleName is the roleName from server
     * @param objectOutputStream is the outputStream to the server
     * @param objectInputStream is the inputStream from the server
     * @return a role by the inputs
     */
    public Role generateRole(RoleNames roleName , ObjectOutputStream objectOutputStream , ObjectInputStream objectInputStream){
        if(roleName == RoleNames.godFather){
            return new GodFather(objectInputStream , objectOutputStream , userName , inputProducer);
        }
        else if(roleName == RoleNames.doctorLector){
            return new DoctorLector(objectInputStream , objectOutputStream ,userName , inputProducer);
        }
        else if(roleName == RoleNames.detective){
            return new Detective(objectInputStream , objectOutputStream, userName , inputProducer);
        }
        else if(roleName ==  RoleNames.mafia){
            return new Mafia(objectInputStream , objectOutputStream , userName , inputProducer);
        }
        else if(roleName == RoleNames.mayor){
            return new Mayor(objectInputStream , objectOutputStream , userName , inputProducer);
        }
        else if(roleName == RoleNames.normalCitizen){
            return new NormalCitizen(objectInputStream , objectOutputStream);
        }
        else if(roleName == RoleNames.professional){
            return new Professional(objectInputStream , objectOutputStream , userName , inputProducer);
        }
        else if(roleName == RoleNames.therapist){
            return new Therapist(objectInputStream , objectOutputStream , userName , inputProducer);
        }
        else if(roleName == RoleNames.toughGuy){
            return new ToughGuy(objectInputStream , objectOutputStream , userName , inputProducer);
        }
        else if(roleName == RoleNames.townDoctor){
            return new TownDoctor(objectInputStream , objectOutputStream , userName , inputProducer);
        }
        else{
            return null;
        }
    }

    /**
     * prints the string in a command from server
     * @param command is the command contains the string
     */
    public void printServerToClientString(Command command){
        System.out.println(command.getCommandNeededThings());
    }

    /**
     * player informs the server that he is ready for the game
     */
    private void getReady(){

        System.out.println("Enter ready to inform the god that you are ready : ");
        String input = "";

        do {

            while (! inputProducer.hasNext()){

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            input = inputProducer.consumeInput();

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

    /**
     * player receives command from server
     * @return the received command from server
     */
    private Command receiveServerCommand(){

        try {
            return (Command) objectInputStream.readObject();
        } catch (IOException e) {
            disConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * sends a command to the server
     * @param command is the sending command
     */
    private void sendCommandToServer(Command command){

        try {
            objectOutputStream.writeObject(command);
        } catch (IOException e) {
            disConnection();
        }

    }

    /**
     * prints the input message
     * @param message is the input message
     */
    private void printMessage(Message message){
        System.out.println(message.getSenderName() + ": "+ message.getMessageText());
    }

    /**
     * player dies ( the program still runs ) and the reason of death will be printed
     * @param command is the death command from server
     */
    private void die(Command command){
        isAlive = false;
        System.out.println("you are dead");
        System.out.println("reason : " + command.getCommandNeededThings());
    }

    /**
     * prints the result of the game and end of the program
     * @param command is the end of the game command
     */
    private void endOfTheGame(Command command){
        System.out.println(command.getCommandNeededThings());
        System.exit(0);
    }

    /**
     * player is disconnected from server and the program ends
     */
    private void disConnection(){
        System.err.println("you are disconnected from server .");
        playerExitsTheGame();
    }
}
