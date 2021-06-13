package rad.heydari.mohammad.midterm.project.mafia.commandThings;

import java.io.Serializable;
/** an enum for the type of the commands
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 */
public enum CommandTypes implements Serializable {

    //server to client command types:

    determineYourUserName,
    repetitiousUserName,
    yourUserNameIsSet,
    takeYourRole,
    vote,
    votingResult,
    doYourAction,
    itIsNight,
    newMessage,
    chatRoomStarted,
    chatRoomIsClosed,
    serverToClientString,
    waitingForClientToGetReady,
    youAreDead,
    endOfTheGame,
    youAreMutedForTomorrow,
    // client to server command type :
    setMyUserName,
    iExitTheGame,
    messageToOthers,
    imReady, // dont want to chat any more
    iVote,
    iDoMyAction,
    mayorSaysLynch,
    mayorSaysDontLynch,
}
