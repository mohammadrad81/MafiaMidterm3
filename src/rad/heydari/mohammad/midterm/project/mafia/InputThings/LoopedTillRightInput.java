package rad.heydari.mohammad.midterm.project.mafia.InputThings;

import java.util.Scanner;

/**
 * makes the client to enter the valid input
 * if enters something else
 * repeats
 */
public class LoopedTillRightInput {

    private Scanner scanner;

    public LoopedTillRightInput(){
       scanner = new Scanner(new UnClosableStream(System.in));
    }
    public String stringInput(){
        String input = null;

        input = scanner.nextLine();

        return input;
    }

    public int intInput(){
        boolean doneCorrectly = false;
        int input = 0;
        while (! doneCorrectly){
            try{
                input = Integer.parseInt(stringInput());
                doneCorrectly = true;
            }catch (NumberFormatException e){
                System.err.println("the input is not a number , please try again : ");
                doneCorrectly = false;
            }
        }
        return input;
    }

    public int rangedIntInput(int minimum , int maximum){
        boolean doneCorrectly = false;
        int input = 0;
        while (! doneCorrectly){
            input = intInput();
            if(input > maximum || input < minimum){
                System.out.println("the input is not in range (minimum : " + minimum + " & maximum : " +maximum);
            }
            else{
                doneCorrectly = true;
            }
        }
        return input;
    }

    public void resetTheInput(){
        this.scanner = new Scanner(new UnClosableStream(System.in));
    }


}
