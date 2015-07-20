package gcm.play.android.samples.com.gcmquickstart;

/**
 * Created by dk on 7/10/2015.
 */
public class Messages {

    String messageId;
    String messageFrom;
    String messageTo;
    String messageText;
public Messages(String messageText){
    super();
    setMessageText(messageText);

}
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }
}
