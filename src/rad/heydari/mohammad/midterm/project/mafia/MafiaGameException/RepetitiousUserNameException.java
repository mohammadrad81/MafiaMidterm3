package rad.heydari.mohammad.midterm.project.mafia.MafiaGameException;
/**
 * an exception for the situation , the username picked by the player , is repetitious
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class RepetitiousUserNameException extends Exception{

    public RepetitiousUserNameException(String message){
        super(message);
    }

}
