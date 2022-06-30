package com.petsadoption.Objects;

public class Message {
    String uid;
    String message;
    String authorEmail;
    String authorPhotoUrl;
    long timestamp;

    public Message() {
    }

    public Message(String uid, String message, String authorEmail, String authorPhotoUrl, long timestamp) {
        this.uid = uid;
        this.message = message;
        this.authorEmail = authorEmail;
        this.authorPhotoUrl = authorPhotoUrl;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getAuthorPhotoUrl() {
        return authorPhotoUrl;
    }

    public void setAuthorPhotoUrl(String authorPhotoUrl) {
        this.authorPhotoUrl = authorPhotoUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
