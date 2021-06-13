package rad.heydari.mohammad.midterm.project.mafia.roleThings;

import java.io.Serializable;
/**
 * class for storing the roles of the players (client side)
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */

public interface Role extends Serializable {
    /**
     *
     * @return the role as a string
     */
    public String getRoleString();
}
