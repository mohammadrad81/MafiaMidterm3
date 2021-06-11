package rad.heydari.mohammad.midterm.project.mafia.workWithFileThings;

import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FileUtils {

    private String fileName;
    private File file;
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

    public void printAllMessages(){

        synchronized (file){
            try (Scanner scanner = new Scanner(file)){
                while (scanner.hasNextLine()){
                    System.out.println(scanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
