package chat.model;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String from;
    private String to;
    private String text;
    private boolean isRead;

    public Message(String from, String to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
        isRead = false;
    }

    public Message(String from, String to, String text, boolean isRead) {
        this(from, to, text);
        this.isRead = isRead;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
