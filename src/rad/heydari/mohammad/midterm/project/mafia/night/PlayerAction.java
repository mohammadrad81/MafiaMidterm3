package rad.heydari.mohammad.midterm.project.mafia.night;

import java.io.Serializable;

public class PlayerAction implements Serializable {

    private PlayersActionTypes PlayerActionType;
    private String nameOfThePlayerActionHappensTo;
    private String actionDoerName;
    public PlayerAction(PlayersActionTypes PlayerActionType, String nameOfThePlayerActionHappensTo) {
        this.PlayerActionType = PlayerActionType;
        this.nameOfThePlayerActionHappensTo = nameOfThePlayerActionHappensTo;
    }

    public PlayerAction(PlayersActionTypes playerActionType){
        this.PlayerActionType = playerActionType;
    }

    public PlayerAction(PlayersActionTypes playerActionType ,
                        String nameOfThePlayerActionHappensTo ,
                        String actionDoerName){

        this.PlayerActionType = playerActionType;
        this.nameOfThePlayerActionHappensTo = nameOfThePlayerActionHappensTo;
        this.actionDoerName = actionDoerName;
    }

    public PlayersActionTypes getPlayerActionType() {
        return PlayerActionType;
    }

    public String getNameOfThePlayerActionHappensTo() {
        return nameOfThePlayerActionHappensTo;
    }

    public String getActionDoerName() {
        return actionDoerName;
    }
}
