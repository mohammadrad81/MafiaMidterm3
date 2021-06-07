package rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.InputThings.LoopedTillRightInput;

import java.util.Scanner;

public class RunnableInputTaker implements Runnable{

    private InputProducer inputProducer;

    public RunnableInputTaker(InputProducer inputProducer){
        this.inputProducer = inputProducer;
    }
    @Override
    public void run() {
        inputProducer.startTakingInputs();
    }
}
