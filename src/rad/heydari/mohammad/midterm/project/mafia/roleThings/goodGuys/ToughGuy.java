package rad.heydari.mohammad.midterm.project.mafia.roleThings.goodGuys;

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

public class ToughGuy extends Actionable implements GoodGuys {
    private int askedToShowDeadRoles;
    private LoopedTillRightInput loopedTillRightInput;
    public ToughGuy(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream , String userName) {
        super(objectInputStream, objectOutputStream , "tough guy" , userName);
        this.loopedTillRightInput = new LoopedTillRightInput();
    }

    @Override
    public void action(Command command) {
        boolean correctlyDone = false;
        boolean wantsToReveal = false;
        while (! correctlyDone){
            if(askedToShowDeadRoles < 2){
                System.out.println("do you want to ask to reveal the dead one roles ?(y/n) :");
                char input = loopedTillRightInput.stringInput().charAt(0);
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

        if(wantsToReveal){
            try {
                getObjectOutputStream().writeObject(new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(PlayersActionTypes.toughGuySaysShowDeadRoles,
                                null)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                getObjectOutputStream().writeObject(new Command(CommandTypes.iDoMyAction ,
                        new PlayerAction(null ,
                                null)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
