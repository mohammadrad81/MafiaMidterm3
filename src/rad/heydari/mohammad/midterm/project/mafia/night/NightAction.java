package rad.heydari.mohammad.midterm.project.mafia.night;

import java.io.Serializable;

public class NightAction implements Serializable {

    private NightActionTypes nightActionType;
    private String nameOfThePlayerActionHappensTo;

    public NightAction(NightActionTypes nightActionType, String nameOfThePlayerActionHappensTo) {
        this.nightActionType = nightActionType;
        this.nameOfThePlayerActionHappensTo = nameOfThePlayerActionHappensTo;
    }

    public NightActionTypes getNightActionType() {
        return nightActionType;
    }

    public String getNameOfThePlayerActionHappensTo() {
        return nameOfThePlayerActionHappensTo;
    }


}
