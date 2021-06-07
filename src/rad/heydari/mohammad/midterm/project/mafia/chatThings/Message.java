package rad.heydari.mohammad.midterm.project.mafia.chatThings;

import java.io.Serializable;

public class Message implements Serializable {
    private String senderName;
    private String message;

    public Message(String senderName, String message) {
        this.senderName = senderName;
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessageText() {
        return message;
    }

}
