package rad.heydari.mohammad.midterm.project.mafia.InputThings;

import java.util.ArrayList;

/**
 * the class for store , consume the inputs of the client ( technically a buffer )
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class InputProducer {

    private ArrayList<String> inputs = null;
    private LoopedTillRightInput loopedTillRightInput;
    private boolean takeInput = true;

    /**
     * constructor for the input producer
     * @param loopedTillRightInput is the objects that takes input from the client
     * @see LoopedTillRightInput
     */
    public InputProducer(LoopedTillRightInput loopedTillRightInput){
        inputs = new ArrayList<>();
        this.loopedTillRightInput = loopedTillRightInput;
    }

    /**
     * in a loop , takes inputs from the client
     */
    public void startTakingInputs(){
        while (takeInput){
            String input = loopedTillRightInput.stringInput();
            storeInput(input);
        }
    }

    /**
     * adds the input string in the inputs arrayList
     * @param input is the input string
     */
    private void storeInput(String input){
        synchronized (input){
            inputs.add(input);
        }
    }

    /**
     * @return true if there is at least one input , else false
     */
    public boolean hasNext(){
        synchronized (inputs){
            if(inputs.size() > 0){
                return true;
            }
            return false;
        }
    }

    /**
     *
     * @return the first existing input from the arrayList and removes it from the input arrayList
     */
    public String consumeInput(){
        String consumingString = null;
        if(hasNext()){
            synchronized (inputs){
                consumingString = inputs.get(0);
                inputs.remove(0);
            }
        }
        return consumingString;
    }

    public void clearInputs(){
        synchronized (inputs){
            inputs = new ArrayList<>();
        }
    }

    /**
     * stops taking input from the client
     */
    public void stopTakingInput(){
        takeInput = false;
    }
}