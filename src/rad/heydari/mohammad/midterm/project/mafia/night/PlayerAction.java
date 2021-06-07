package rad.heydari.mohammad.midterm.project.mafia.night;

import java.io.Serializable;

public class PlayerAction implements Serializable {

    private PlayersActionTypes nightActionType;
    private String nameOfThePlayerActionHappensTo;

    public PlayerAction(PlayersActionTypes nightActionType, String nameOfThePlayerActionHappensTo) {
        this.nightActionType = nightActionType;
        this.nameOfThePlayerActionHappensTo = nameOfThePlayerActionHappensTo;
    }

    public PlayersActionTypes getNightActionType() {
        return nightActionType;
    }

    public String getNameOfThePlayerActionHappensTo() {
        return nameOfThePlayerActionHappensTo;
    }


}
