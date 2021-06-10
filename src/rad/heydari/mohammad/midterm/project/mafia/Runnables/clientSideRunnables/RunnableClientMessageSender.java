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

public class RunnableClientMessageSender implements Runnable{
    private String userName;
    private ObjectOutputStream objectOutputStream;
//    private LoopedTillRightInput loopedTillRightInput;
//    private BufferedReader bufferedReader;

    private long startChatTimeStamp;
    private long nowTimeStamp;
    private InputProducer inputProducer;
    private long startSecond;
    private long timeLimit;

    public RunnableClientMessageSender(String userName, ObjectOutputStream objectOutputStream , InputProducer inputProducer) {
        this.userName = userName;
        this.objectOutputStream = objectOutputStream;
//        this.loopedTillRightInput = new LoopedTillRightInput();
//        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        this.startChatTimeStamp = java.time.Instant.now().getEpochSecond();

        this.inputProducer = inputProducer;

        this.timeLimit = 3000;
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

            if( input.equals("ready")) {
                try {
                    objectOutputStream.writeObject(new Command(CommandTypes.imReady , null));
                    sentReady = true;
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else{

                try {
                    objectOutputStream.writeObject(new Command(CommandTypes.messageToOthers , new Message(userName , input)));
                } catch (IOException e) {
                    System.out.println("there is a problem with your connection");
                    e.printStackTrace();
                }
            }

        }while (input != "ready" && (java.time.Instant.now().getEpochSecond() < startChatTimeStamp + 3000));

        if(!sentReady ){

            try {
                objectOutputStream.writeObject(new Command(CommandTypes.imReady , null));
            } catch (IOException e) {
                e.printStackTrace();
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
