package rad.heydari.mohammad.midterm.project.mafia.workWithFileThings;

import rad.heydari.mohammad.midterm.project.mafia.chatThings.Message;

import java.io.*;

public class FileUtils {

    private String fileName;
    private File file;
    public FileUtils(String userName){
        fileName = userName + "Messages.bin";
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
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file , true))){
                objectOutputStream.writeObject(message);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void printAllMessages(){
        synchronized (file){
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))){
                Message message = null;
                while (true){
                    try {
                        message = (Message) objectInputStream.readObject();
                        System.out.println("// HISTORY MESSAGE :: " +
                                message.getSenderName() +
                                " : " +
                                message.getMessageText());
                    }catch (EOFException e){
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
