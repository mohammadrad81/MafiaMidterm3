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

public class RunnableClientVote implements Runnable{
    private String voterName;
    private ObjectOutputStream objectOutputStream ;
//    private LoopedTillRightInput loopedTillRightInput;
//    private BufferedReader bufferedReader;
    private ArrayList<String> otherPlayersNames;
    private InputProducer inputProducer;
    private long startSecond;
    private long timeLimit;
    public RunnableClientVote(String voterName ,ArrayList<String> otherPlayersNames , ObjectOutputStream objectOutputStream , InputProducer inputProducer){
        this.voterName = voterName;
        this.otherPlayersNames = otherPlayersNames;
        this.objectOutputStream = objectOutputStream;
//        this.loopedTillRightInput = new LoopedTillRightInput();
//        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        this.inputProducer = inputProducer;
        this.timeLimit = 120;
    }


    @Override
    public void run() {
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

        if(! correctlyDone){
            voteCommand = new Command(CommandTypes.iVote , new Vote(voterName , null));
        }

        try {
            objectOutputStream.writeObject(voteCommand);
        } catch (IOException e) {
            e.printStackTrace();
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
