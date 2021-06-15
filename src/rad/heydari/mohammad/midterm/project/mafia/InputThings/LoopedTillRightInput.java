package rad.heydari.mohammad.midterm.project.mafia.InputThings;

import rad.heydari.mohammad.midterm.project.mafia.MafiaGameException.NoUserFileUtilException;
import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;
import rad.heydari.mohammad.midterm.project.mafia.workWithFileThings.FileUtils;

import java.util.Scanner;

/**
 * makes the client to enter the valid input
 * if enters something else
 * repeats
 *
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 *
 */
public class LoopedTillRightInput {

    private Scanner scanner;
    private FileUtils fileUtils;

    /**
     * constructor for the class
     * init the scanner
     */
    public LoopedTillRightInput(){
        scanner = new Scanner(System.in);
    }

    /**
     * scans a string from system input
     * @return the input string , but if it is "exit" or "HISTORY" ,
     * recursively does the command and takes the input again
     */
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

    /**
     * only an integer is valid
     * @return the input integer
     */
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

    /**
     * only an integer in a range is valid
     * @param minimum is the minimum valid amount
     * @param maximum is the maximum valid amount
     * @return the input number
     */
    public int rangedIntInput(int minimum , int maximum){
        boolean doneCorrectly = false;
        int input = 0;
        while (! doneCorrectly){
            input = intInput();
            if(input > maximum || input < minimum){
                System.out.println("the input is not in range (minimum : " + minimum + " & maximum : " +maximum + " ) " );
            }
            else{
                doneCorrectly = true;
            }
        }
        return input;
    }

    /**
     * creates a file for storing the messages
     * @param userName the username of the user
     */
    public void createFileUtils(String userName){
        this.fileUtils = new FileUtils(userName);
    }

    /**
     * saves the message to the file
     * @param message is the message going to save
     * @throws NoUserFileUtilException
     * @see FileUtils
     */
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

    /**
     * prints all the messages from the file
     */
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
