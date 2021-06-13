package rad.heydari.mohammad.midterm.project.mafia.workWithFileThings;

import rad.heydari.mohammad.midterm.project.mafia.MafiaGameException.NoUserFileUtilException;
import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
/**
 * a class for working with file and saving messages
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class FileUtils {

    private String fileName;
    private File file;

    /**
     * simple constructor
     * @param userName is the name of the player , the fileUtil saves messages for
     */
    public FileUtils(String userName){
        fileName = userName + "Messages.txt";
        file = new File(fileName);
        if(file.exists()){
            file.delete();

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * saves a message for player
     * @param message is the message the player received
     */
    public void saveMessage(Message message){
        synchronized (file){
            try (FileWriter fileWriter = new FileWriter(file , true)){
                fileWriter.write(("// HISTORY MESSAGE :: " + message.getSenderName() + " : " + message.getMessageText() + "\n"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * prints all messages for the player
     */
    public void printAllMessages(){

        synchronized (file){
            try (Scanner scanner = new Scanner(file)){
                while (scanner.hasNextLine()){
                    System.out.println(scanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                throw new NoUserFileUtilException("! there is no file yet!");
            }
        }
    }


}
