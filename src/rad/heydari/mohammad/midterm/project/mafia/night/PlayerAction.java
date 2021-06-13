package rad.heydari.mohammad.midterm.project.mafia.night;

import java.io.Serializable;
/**
 * a class for actions of the players
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @see PlayersActionTypes
 */
public class PlayerAction implements Serializable {

    private PlayersActionTypes PlayerActionType;
    private String nameOfThePlayerActionHappensTo;
    private String actionDoerName;

    /**
     * constructor for the action
     * @param PlayerActionType the type of the action
     * @param nameOfThePlayerActionHappensTo is the name of the player , the action happens to
     */
    public PlayerAction(PlayersActionTypes PlayerActionType, String nameOfThePlayerActionHappensTo) {
        this.PlayerActionType = PlayerActionType;
        this.nameOfThePlayerActionHappensTo = nameOfThePlayerActionHappensTo;
    }

    /**
     * constructor for the action
     * @param playerActionType is the type of the action
     */
    public PlayerAction(PlayersActionTypes playerActionType){
        this.PlayerActionType = playerActionType;
    }

    /**
     * constructor for the action
     * @param playerActionType is the type of the action
     * @param nameOfThePlayerActionHappensTo is the name of the player , the action happens to
     * @param actionDoerName is the name of the action doer
     */
    public PlayerAction(PlayersActionTypes playerActionType ,
                        String nameOfThePlayerActionHappensTo ,
                        String actionDoerName){

        this.PlayerActionType = playerActionType;
        this.nameOfThePlayerActionHappensTo = nameOfThePlayerActionHappensTo;
        this.actionDoerName = actionDoerName;
    }

    /**
     *
     * @return the type of the action
     */
    public PlayersActionTypes getPlayerActionType() {
        return PlayerActionType;
    }

    /**
     *
     * @return the name of the player , the action happens to
     */
    public String getNameOfThePlayerActionHappensTo() {
        return nameOfThePlayerActionHappensTo;
    }

    /**
     *
     * @return the name of the action doer
     */
    public String getActionDoerName() {
        return actionDoerName;
    }
}
