package rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables;

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
    private BufferedReader bufferedReader;
    private ArrayList<String> otherPlayersNames;

    public RunnableClientVote(String voterName ,ArrayList<String> otherPlayersNames , ObjectOutputStream objectOutputStream){
        this.voterName = voterName;
        this.otherPlayersNames = otherPlayersNames;
        this.objectOutputStream = objectOutputStream;
//        this.loopedTillRightInput = new LoopedTillRightInput();
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }


    @Override
    public void run() {
        System.out.println("0- no body");
        for(int i = 1; i <= otherPlayersNames.size(); i++){
            System.out.println(i + "- " + otherPlayersNames.get(i-1));
        }

        int input = 0;
        Command voteCommand = null;


            try {
                input = Integer.parseInt(bufferedReader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }


        if(input == 0){
            voteCommand = new Command(CommandTypes.iVote , new Vote(voterName , null));
        }

        else{
            String suspectName = otherPlayersNames.get(input - 1);
            voteCommand = new Command(CommandTypes.iVote , new Vote(voterName , suspectName));
        }

        try {
            objectOutputStream.writeObject(voteCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
