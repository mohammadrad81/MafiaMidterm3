package rad.heydari.mohammad.midterm.project.mafia.clientThings;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.LoopedTillRightInput;
import rad.heydari.mohammad.midterm.project.mafia.colors.ColorCodes;
import rad.heydari.mohammad.midterm.project.mafia.gameThings.ClientSideGame;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
/** a class for the client side of the mafia game
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */

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

    /**
     * the main method for starting the game
     * @param args is the input arguments ( no usage )
     */
    public static void main(String[] args) {

        loopedTillRightInput = new LoopedTillRightInput();


        System.out.println(ColorCodes.ANSI_CYAN +
                "! WELCOME TO RAD GAME CLUB !" +
                ColorCodes.ANSI_RESET);
        System.out.println(ColorCodes.ANSI_GREEN +
                "PLEASE ENTER THE PORT : (8989 FOR MAFIA GAME)" +
                ColorCodes.ANSI_RESET);
        port = loopedTillRightInput.rangedIntInput(MINIMUM_PORT_NUMBER , MAXIMUM_PORT_NUMBER);

        System.out.println(ColorCodes.ANSI_YELLOW +
                "waiting to get connected ..." +
                ColorCodes.ANSI_RESET);

        waitTillGetConnected();

        initStreams();

        System.out.println(ColorCodes.ANSI_GREEN + "connected." + ColorCodes.ANSI_RESET);

        ClientMafiaGameLogic clientMafiaGameLogic = new ClientMafiaGameLogic(socket , objectInputStream , objectOutputStream);

        clientMafiaGameLogic.play();

    }

    /**
     * if the server is not still turned on , the client waits for the server and every 0.2 second ,
     * checks the server is on or not , if it is on , connects to it
     */
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

    /**
     * inits the streams of the connection
     */

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
