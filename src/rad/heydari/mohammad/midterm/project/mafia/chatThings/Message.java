package rad.heydari.mohammad.midterm.project.mafia.chatThings;

import java.io.Serializable;

/**
 * class for messaging between clients
 * this is an immutable class
 * @author Mohammad Heydari Rad
 * @since 6/11/2021
 * @version 1.0
 */
public class Message implements Serializable {
    private String senderName;
    private String message;

    /**
     * constructor for messages
     * @param senderName is the sender of the message
     * @param message is the text of the message
     */
    public Message(String senderName, String message) {
        this.senderName = senderName;
        this.message = message;
    }

    /**
     * getter for the name of the sender
     * @return the name of the sender of the message
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * getter for the text of the message
     * @return the text of the message
     */
    public String getMessageText() {
        return message;
    }

}
