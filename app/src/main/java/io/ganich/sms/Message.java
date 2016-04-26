package io.ganich.sms;

import java.io.Serializable;

/**
 * Created by Ganitzsh on 4/11/16.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 7526471155622776147L;

    private String sender;
    private String body;
    private Boolean you;
    private Boolean first;

    public Message() {
    }

    public Message(String sender, String body, Boolean you, Boolean first) {
        this.sender = sender;
        this.body = body;
        this.you = you;
        this.first = first;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getYou() {
        return you;
    }

    public void setYou(Boolean you) {
        this.you = you;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }
}
