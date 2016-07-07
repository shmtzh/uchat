package com.roket.shmtzh.uchat.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shmtzh on 7/4/16.
 */
public class Message {
    @SerializedName("message")
    private String message;
    @SerializedName("date")
    private long date;
    @SerializedName("type")
    private String type;

    public Message(String message, long date, String type) {
        this.message = message;
        this.date = date;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
