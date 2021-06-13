package rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
/**
 * a class for the player to send his messages during the chat
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class RunnableClientMessageSender implements Runnable{
    private String userName;
    private ObjectOutputStream objectOutputStream;

    private InputProducer inputProducer;
    private long startSecond;
    private long timeLimit;

    /**
     * simple constructor
     * @param userName is the userName of the message sender
     * @param objectOutputStream is the outputStream to communicate with server ( sending messages by it )
     * @param inputProducer is the input producer for players inputs
     * @see InputProducer
     */
    public RunnableClientMessageSender(String userName, ObjectOutputStream objectOutputStream , InputProducer inputProducer) {
        this.userName = userName;
        this.objectOutputStream = objectOutputStream;

        this.inputProducer = inputProducer;

        this.timeLimit = 300;
    }

    @Override
    public void run() {
        startNow();

        String input = null;
        boolean sentReady = false;
        do{

//            input = loopedTillRightInput.stringInput().trim();
           while (! isTimeOver(timeLimit)){
               if(inputProducer.hasNext()){
                   input = inputProducer.consumeInput();
                   break;
               }
               else {
                   try {
                       Thread.sleep(200);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
           }

           if(isTimeOver(timeLimit)){
               break;
           }

            if( input.equals("ready")) {
                try {
                    objectOutputStream.writeObject(new Command(CommandTypes.imReady , null));
                    sentReady = true;
                    break;
                } catch (IOException e) {
                    System.err.println("! you are disconnected from server !");
                    System.exit(0);
                }
            }

            else{

                try {
                    objectOutputStream.writeObject(new Command(CommandTypes.messageToOthers , new Message(userName , input)));
                } catch (IOException e) {
                    System.err.println("! you are disconnected from server !");
                    System.exit(0);
                }
            }

        }while (! isTimeOver(timeLimit));

        if(!sentReady ){

            try {
                objectOutputStream.writeObject(new Command(CommandTypes.imReady , null));
            } catch (IOException e) {
                System.err.println("! you are disconnected from server !");
                System.exit(0);
            }

        }

    }

    public void startNow(){
        startSecond = java.time.Instant.now().getEpochSecond();
    }

    public boolean isTimeOver(long timeLimit){
        long nowSecond = java.time.Instant.now().getEpochSecond();
        if(nowSecond >= startSecond + timeLimit){
            return true;
        }
        else {
            return false;
        }
    }
}
