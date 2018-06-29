package hr.ferit.vedran.sharetaxi.model;

import java.util.Date;


public class ChatMessage {
    private String id;
    private String text;
    private String userName;
    private String userId;
    private long timestamp;

    public ChatMessage(String id, String text, String userName, String userId) {
        this.id = id;
        this.text = text;
        this.userName = userName;
        this.userId = userId;
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Europe/Zagreb"));
        this.timestamp = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId () {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
