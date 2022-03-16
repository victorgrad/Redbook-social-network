package ro.ubb.map.gtsn.guitoysocialnetwork.domain;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;

import java.time.LocalDateTime;

public class Notification {
    String content;
    String type;
    LocalDateTime localDateTime;

    public Notification(String content, String type, LocalDateTime localDateTime) {
        this.content = content;
        this.type = type;
        this.localDateTime = localDateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString(){
        StringBuilder text = new StringBuilder();
        switch (type) {
            case Constants.EVENTNOTIFICATION -> {
                return text.append("Event: ").append(content).toString();
            }
            case Constants.FRIENDSHIPNOTIFICATION -> {
                return text.append("Friend Request: ").append(content).toString();
            }
            case Constants.MESSAGENOTIFICATION -> {
                return text.append("Message: ").append(content).toString();
            }
            default -> {
                return text.append(content).toString();
            }
        }
    }
}


