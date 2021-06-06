package rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

public class RunnableClientMessageSender implements Runnable{
    private String userName;
    private ObjectOutputStream objectOutputStream;
//    private LoopedTillRightInput loopedTillRightInput;
    private BufferedReader bufferedReader;
    public RunnableClientMessageSender(String userName, ObjectOutputStream objectOutputStream) {
        this.userName = userName;
        this.objectOutputStream = objectOutputStream;
//        this.loopedTillRightInput = new LoopedTillRightInput();
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        String input = null;

        do{

//            input = loopedTillRightInput.stringInput().trim();
            try {
                input = bufferedReader.readLine().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if( input.equals("ready")) {
                try {
                    objectOutputStream.writeObject(new Command(CommandTypes.imReady , null));
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
        }while (input != "ready");


    }
}
