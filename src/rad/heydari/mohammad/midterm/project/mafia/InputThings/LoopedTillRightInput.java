package rad.heydari.mohammad.midterm.project.mafia.InputThings;

import rad.heydari.mohammad.midterm.project.mafia.MafiaGameException.NoUserFileUtilException;
import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;
import rad.heydari.mohammad.midterm.project.mafia.workWithFileThings.FileUtils;

import java.util.Scanner;

/**
 * makes the client to enter the valid input
 * if enters something else
 * repeats
 */
public class LoopedTillRightInput {

    private Scanner scanner;
    private FileUtils fileUtils;

    public LoopedTillRightInput(){
        scanner = new Scanner(new UnClosableStream(System.in));
    }
    public String stringInput(){
        String input = null;
        input = scanner.nextLine();
        if(input.equals("exit")){
            System.out.println("you exited the game.");
            System.exit(0);
        }
        else if(input.equals("HISTORY")){
            try {
                printAllMessages();
            }catch (NoUserFileUtilException e){
                System.err.println("! there is no history yet !");
            }

            input = stringInput();
        }
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

    public void createFileUtils(String userName){
        this.fileUtils = new FileUtils(userName);
    }

    public void saveMessage(Message message) throws NoUserFileUtilException {
        if(fileUtils == null){
            throw new NoUserFileUtilException("this input taker has no file util (no username is set for it).");
        }
        else {
            synchronized (fileUtils){
                fileUtils.saveMessage(message);
            }
        }

    }

    public void printAllMessages(){
        if (fileUtils == null){
                throw new NoUserFileUtilException("this input taker has no file util (no username is set for it).");
        }
        else {
            synchronized (fileUtils){
                fileUtils.printAllMessages();
            }
        }
    }
}
