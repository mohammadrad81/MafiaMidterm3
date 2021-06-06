package rad.heydari.mohammad.midterm.project.mafia.clientThings;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.LoopedTillRightInput;
import rad.heydari.mohammad.midterm.project.mafia.gameThings.ClientSideGame;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSidePlayer {

    private static LoopedTillRightInput loopedTillRightInput;
//
    private static Socket socket;
    private static int port;
    private static String IP = "127.0.0.1";
//
    private static ObjectOutputStream objectOutputStream;
    private static ObjectInputStream objectInputStream;
//
    private static final int MINIMUM_PORT_NUMBER = 8000;
    private static final int MAXIMUM_PORT_NUMBER = 63000;

    private static ClientSideGame clientSideGame;

    public static void main(String[] args) {

        loopedTillRightInput = new LoopedTillRightInput();


        System.out.println("! WELCOME TO RAD GAME CLUB !");
        System.out.println("PLEASE ENTER THE PORT : (8989 FOR MAFIA GAME)");
        port = loopedTillRightInput.rangedIntInput(MINIMUM_PORT_NUMBER , MAXIMUM_PORT_NUMBER);

        System.out.println("waiting to get connected ...");

        waitTillGetConnected();

        System.out.println("connected.");

        initStreams();

        ClientMafiaGameLogic clientMafiaGameLogic = new ClientMafiaGameLogic(socket , objectInputStream , objectOutputStream);

        clientMafiaGameLogic.play();

    }


    public static void waitTillGetConnected(){
        while (socket == null){
            try {

                Thread.sleep(200);
                socket = new Socket(IP , port);

            } catch (ConnectException e){
                // the socket is trying to get connected
            }
            catch (IllegalArgumentException e){
                System.err.println("the port is not valid");
                System.out.println("please enter another one :");
                port = loopedTillRightInput.rangedIntInput(MINIMUM_PORT_NUMBER , MAXIMUM_PORT_NUMBER);
            } catch(UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void initStreams(){
        try {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            outputStream.flush();

            objectOutputStream = new ObjectOutputStream(outputStream);
            objectInputStream = new ObjectInputStream(inputStream);

            objectOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
