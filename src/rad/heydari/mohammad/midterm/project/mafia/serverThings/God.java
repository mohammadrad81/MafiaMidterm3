package rad.heydari.mohammad.midterm.project.mafia.serverThings;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.LoopedTillRightInput;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Demand;
import rad.heydari.mohammad.midterm.project.mafia.gameThings.ServerSideGame;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class God {
//    private static ArrayList<PlayerHandler> playerHandlers;
    private static int playersCount;
    private static LoopedTillRightInput loopedTillRightInput;
    private static ArrayList<ServerSidePlayerDetails> playersDetails;
    private static ServerSocket serverSocket;
    private static ServerSideGame serverSideGame;

    public static void main(String[] args) {
//        playerHandlers = new ArrayList<>();
        playersDetails = new ArrayList<>();
        loopedTillRightInput = new LoopedTillRightInput();


        System.out.println("! WELCOME TO RAD MAFIA GAME !");
        System.out.println("! YOU ARE THE GOD OF THE GAME !");

        initPlayersCount();


        try {
            serverSocket = new ServerSocket(8989);

        } catch (IOException e) {
            System.err.println("! the server could not get online !");
            e.printStackTrace();
            System.exit(0);
        }



        while (playersDetails.size() < playersCount){

            Socket socket = null;
            ObjectOutputStream objectOutputStream = null;
            ObjectInputStream objectInputStream = null;
            Socket connection = null;
            try {
                makeAndAddServerSidePlayerDetails(connection = serverSocket.accept());
                System.out.println("new player joined the game : " + connection);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        serverSideGame = new ServerMafiaGameLogic(playersDetails);

        serverSideGame.startTheGame();


    }

    private static void initPlayersCount(){
        System.out.println("please Enter the number of the players : ");
        playersCount = loopedTillRightInput.rangedIntInput(1 , 24);
    }

    public static void doTheCommand(Command command){
        serverSideGame.doTheCommand(command);
    }

    public static void doTheDemand(Demand demand){

    }

    public static void makeAndAddServerSidePlayerDetails(Socket socket){


        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try{

                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                outputStream.flush();

                objectOutputStream = new ObjectOutputStream(outputStream);
                objectInputStream = new ObjectInputStream(inputStream);

                objectOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        ServerSidePlayerDetails serverSidePlayerDetails = new ServerSidePlayerDetails(socket ,objectOutputStream , objectInputStream);
        playersDetails.add(serverSidePlayerDetails);
    }

    public static void removeOfflinePlayerNotifyOthers(ServerSidePlayerDetails removingPlayer){
        serverSideGame.removeOfflinePlayerNotifyOthers(removingPlayer);
    }

}
