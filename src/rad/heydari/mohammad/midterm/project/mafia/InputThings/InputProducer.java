package rad.heydari.mohammad.midterm.project.mafia.InputThings;

import java.util.ArrayList;

public class InputProducer {

    private ArrayList<String> inputs = null;
    private LoopedTillRightInput loopedTillRightInput;
    private boolean takeInput = true;
    public InputProducer(LoopedTillRightInput loopedTillRightInput){
        inputs = new ArrayList<>();
        this.loopedTillRightInput = loopedTillRightInput;
    }

    public void startTakingInputs(){
        while (takeInput){
            String input = loopedTillRightInput.stringInput();
            storeInput(input);
        }
    }

    private void storeInput(String input){
        synchronized (input){
            inputs.add(input);
        }
    }

    public boolean hasNext(){
        synchronized (inputs){
            if(inputs.get(0) != null){
                return true;
            }
            return false;
        }
    }

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

    public void stopTakingInput(){
        takeInput = false;
    }
}