package rad.heydari.mohammad.midterm.project.mafia.MafiaGameException;
/**
 * an exception for the situation , there is no file set for file utils to write and read
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 * @see rad.heydari.mohammad.midterm.project.mafia.workWithFileThings.FileUtils
 */
public class NoUserFileUtilException extends RuntimeException{
    public NoUserFileUtilException(String string){
        super(string);
    }
}
