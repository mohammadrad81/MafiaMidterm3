package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayerAction;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayersActionTypes;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.GoodGuys;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Mayor extends Actionable implements GoodGuys {
//    private Scanner scanner;
    public Mayor(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName , InputProducer inputProducer) {
        super(objectInputStream, objectOutputStream , "mayor" , userName , inputProducer);
//        scanner = new Scanner(System.in);
    }

    @Override
    public void action(Command command) {
        startNow();
        boolean correctlyDone = false;
        char input = 'n';
        Command actionCommand = null;
        while (! correctlyDone){

            if(isTimeOver(getTimeLimit())){
                break;
            }

            System.out.println("do you want to cancel the voting ? (y/n): ");

            while (! isTimeOver(getTimeLimit())){
                if(getInputProducer().hasNext()){
                    input = getInputProducer().consumeInput().charAt(0);
                    break;
                }
                else {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
//                input = scanner.nextLine().charAt(0);


            if(input == 'n'){
                System.out.println("ok , you don't cancel the voting .");

                actionCommand = new Command(CommandTypes.mayorSaysLynch , null);
                correctlyDone = true;
            }
            else if(input == 'y'){
                System.out.println("ok , you , the mayor cancel the voting .");
                actionCommand = new Command(CommandTypes.mayorSaysDontLynch , null);
                correctlyDone = true;
            }
            else {
                System.out.println("not valid input\nplease try again :");
            }
        }

        if(! correctlyDone){
            System.out.println("time out , you didn't choose , so the lynch will be done .");
            actionCommand = new Command(CommandTypes.mayorSaysLynch , null);
        }
        try {
            getObjectOutputStream().writeObject(actionCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
