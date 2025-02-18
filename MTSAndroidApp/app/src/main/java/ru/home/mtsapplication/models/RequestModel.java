package ru.home.mtsapplication.models;


public class RequestModel {

    private String userEmail;
    private String topic;
    private String details;
    private String comments;
    private boolean spam;
    private boolean important;
    private boolean isFull;

    public RequestModel() {}

    public RequestModel(String email, String topic, String details, String comments, boolean spam, boolean important) {
        this.userEmail = email;
        this.topic = topic;
        this.details = details;
        this.comments = comments;
        this.spam = spam;
        this.important = important;
    }

    public RequestModel(String topic, String details, String comments, boolean spam, boolean important) {
        this.topic = topic;
        this.details = details;
        this.comments = comments;
        this.spam = spam;
        this.important = important;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getTopic() {
        return topic;
    }

    public String getDetails() {
        return details;
    }

    public String getComments() {
        return comments;
    }

    public boolean isSpam() {
        return spam;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }
}
