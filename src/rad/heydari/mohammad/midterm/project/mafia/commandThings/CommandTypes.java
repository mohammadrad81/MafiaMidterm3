package rad.heydari.mohammad.midterm.project.mafia.commandThings;

import java.io.Serializable;

public enum CommandTypes implements Serializable {

    //server to client command types:

    determineYourUserName,
    repetitiousUserName,
    yourUserNameIsSet,
    takeYourRole,
//    chatWithOthers,
    vote,
    votingResult,
    doYourAction,
    itIsNight,
    newMessage,
    chatRoomStarted,
    chatRoomIsClosed,
//    mafiaIntroduction,
//    doctorToMayorIntroduction,
//    mayorToDoctorIntroduction,
//    serverToClientMessage,
    serverToClientString,
    waitingForClientToGetReady,
    youAreDead,
    endOfTheGame,

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
