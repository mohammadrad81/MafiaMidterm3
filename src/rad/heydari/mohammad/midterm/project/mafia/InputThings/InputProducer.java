package rad.heydari.mohammad.midterm.project.mafia.InputThings;

import java.util.ArrayList;

public class InputProducer {

    private ArrayList<String> inputs = null;
    private LoopedTillRightInput loopedTillRightInput;
    public InputProducer(){
        inputs = new ArrayList<>();
        loopedTillRightInput = new LoopedTillRightInput();
    }

    public void startTakingInputs(){

        String input = loopedTillRightInput.stringInput();
        storeInput(input);

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

}