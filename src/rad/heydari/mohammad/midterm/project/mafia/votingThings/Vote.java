package rad.heydari.mohammad.midterm.project.mafia.votingThings;

import java.io.Serializable;

public class Vote implements Serializable {

    private String voterName;
    private String suspectName;

    public Vote(String voterName, String suspectName) {
        this.voterName = voterName;
        this.suspectName = suspectName;
    }

    public String getVoterName() {
        return voterName;
    }

    public String getSuspectName() {
        return suspectName;
    }

}
