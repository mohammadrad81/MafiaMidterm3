package rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.votingThings.Vote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * runnable class for player to vote
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public class RunnableClientVote implements Runnable{
    private String voterName;
    private ObjectOutputStream objectOutputStream ;
//    private LoopedTillRightInput loopedTillRightInput;
//    private BufferedReader bufferedReader;
    private ArrayList<String> otherPlayersNames;
    private InputProducer inputProducer;
    private long startSecond;
    private long timeLimit;

    /**
     * simple constructor
     * @param voterName is the name of the voter
     * @param otherPlayersNames is the arrayList of other players
     * @param objectOutputStream is the outputStream for communication to server
     * @param inputProducer is the inputProducer of the client ( the player )
     * @see InputProducer
     */
    public RunnableClientVote(String voterName ,
                              ArrayList<String> otherPlayersNames ,
                              ObjectOutputStream objectOutputStream ,
                              InputProducer inputProducer){
        this.voterName = voterName;
        this.otherPlayersNames = otherPlayersNames;
        this.objectOutputStream = objectOutputStream;
        this.inputProducer = inputProducer;
        this.timeLimit = 60;
    }

    /**
     * the run method for voting of the player
     */
    @Override
    public void run() {
        otherPlayersNames.remove(voterName);
        startNow();
        boolean correctlyDone = false;
        int input = 0;
        Command voteCommand = null;

        do{
            System.out.println("0- no body");
            for(int i = 1; i <= otherPlayersNames.size(); i++){
                System.out.println(i + "- " + otherPlayersNames.get(i-1));
            }



            while (! isTimeOver(timeLimit)){
                if(inputProducer.hasNext()){
                    try {
                        input = Integer.parseInt(inputProducer.consumeInput());
                        break;
                    }catch (NumberFormatException e){
                        System.out.println("input is not a number , please try again : ");
                    }
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

            if(input == 0){
                voteCommand = new Command(CommandTypes.iVote , new Vote(voterName , null));
                correctlyDone = true;
            }

            else if (input > 0 && input <= otherPlayersNames.size()){
                String suspectName = otherPlayersNames.get(input - 1);
                voteCommand = new Command(CommandTypes.iVote , new Vote(voterName , suspectName));
                correctlyDone = true;
            }
            else {
                System.out.println("input is not in the valid range , please try again .");
                correctlyDone = false;
            }

        }while (!correctlyDone && !isTimeOver(timeLimit));
        /*
         * if player didn't vote , by default his vote is considered as no one
         */
        if(! correctlyDone){
            voteCommand = new Command(CommandTypes.iVote , new Vote(voterName , null));
        }

        try {
            objectOutputStream.writeObject(voteCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * saves the start moment of the game
     */
    public void startNow(){
        startSecond = java.time.Instant.now().getEpochSecond();
    }

    /**
     * checks if the time is over or not
     * @param timeLimit is the limitation for voting
     * @return true , if the time is over , else false
     */
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
