package rad.heydari.mohammad.midterm.project.mafia.Runnables.clientSideRunnables;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
/**
 * a runnable that constantly takes inputs from the client
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @see InputProducer
 */
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
