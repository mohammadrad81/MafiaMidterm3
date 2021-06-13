package rad.heydari.mohammad.midterm.project.mafia.votingThings;

import java.io.Serializable;
/**
 * a class for votes of players
 * ( this is immutable )
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public class Vote implements Serializable {

    private String voterName;
    private String suspectName;

    /**
     * constructor for vote
     * @param voterName is the name of the player who votes
     * @param suspectName is the name of the player who the voter votes to
     */
    public Vote(String voterName, String suspectName) {
        this.voterName = voterName;
        this.suspectName = suspectName;
    }

    /**
     *
     * @return the name of the voter
     */
    public String getVoterName() {
        return voterName;
    }

    /**
     *
     * @return the name of the suspect
     */
    public String getSuspectName() {
        return suspectName;
    }
}
