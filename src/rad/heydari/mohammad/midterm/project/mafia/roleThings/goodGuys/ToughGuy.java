package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

import rad.heydari.mohammad.midterm.project.mafia.InputThings.InputProducer;
import rad.heydari.mohammad.midterm.project.mafia.InputThings.LoopedTillRightInput;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.Command;
import rad.heydari.mohammad.midterm.project.mafia.commandThings.CommandTypes;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayerAction;
import rad.heydari.mohammad.midterm.project.mafia.night.PlayersActionTypes;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.Actionable;
import rad.heydari.mohammad.midterm.project.mafia.roleThings.GoodGuys;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * class for the role , ToughGuy
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class ToughGuy extends Actionable implements GoodGuys {
    private int askedToShowDeadRoles;
    /**
     * constructor for the role
     * @param objectInputStream the InputStream to communicate with server
     * @param objectOutputStream the outputStream to communicate with server
     * @param userName is the username of the player
     * @param inputProducer is the inputProducer of the player
     */
    public ToughGuy(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName , InputProducer inputProducer) {
        super(objectInputStream, objectOutputStream , "tough guy" , userName , inputProducer);
    }
    /**
     * the action of the player
     * @param command the command that contains the needed things for the action
     */
    @Override
    public void action(Command command) {
        startNow();
        boolean correctlyDone = false;
        boolean wantsToReveal = false;
        char input = 'n';
        while (! correctlyDone){

            if(isTimeOver(getTimeLimit())){
                break;
            }

            if(askedToShowDeadRoles < 2){
                System.out.println("do you want to ask to reveal the dead one roles ?(y/n) :");

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

                if(isTimeOver(getTimeLimit())){
                    break;
                }

                if(input == 'y'){
                    System.out.println("ok , after night the dead ones roles will be revealed");
                    wantsToReveal = true;
                    correctlyDone = true;
                    askedToShowDeadRoles ++;
                }
                else if(input == 'n'){
                    System.out.println("ok , not tonight");
                    wantsToReveal = false;
                    correctlyDone = true;
                }
                else {
                    System.out.println(" wrong input , please try again");
                    correctlyDone = false;
                }
            }

            else {
                System.out.println("you already have used your chances , you revealed twice before");
                wantsToReveal = false;
                correctlyDone = true;
            }
        }

        if(! correctlyDone){
            System.out.println("time out , you didn't choose ," +
                    " so by default ," +
                    " the dead roles will not be revealed .");
                wantsToReveal = false;
        }

        if(wantsToReveal){
            try {
                getObjectOutputStream().writeObject(new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.toughGuySaysShowDeadRoles,
                                null)));
            } catch (IOException e) {
                System.err.println("! you are disconnected from server !");
                System.exit(0);
            }
        }
        else {
            try {
                getObjectOutputStream().writeObject(new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(null ,
                                null)));
            } catch (IOException e) {
                System.err.println("! you are disconnected from server !");
                System.exit(0);
            }
        }

    }
}
