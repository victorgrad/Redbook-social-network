package ro.ubb.map.gtsn.guitoysocialnetwork.domain.models;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;
import ro.ubb.map.gtsn.guitoysocialnetwork.domain.Message;

import java.time.LocalDateTime;
import java.util.Set;

public class MessageModel {
    private Long messageId;
    private String from;
    private Set<String> to;
    private String message;
    private LocalDateTime date;
    private Long reply;

    public MessageModel() {
    }

    public MessageModel(Long messageID, String from, Set<String> to, String message, LocalDateTime date, Long reply) {
        this.messageId = messageID;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }

    public MessageModel(Message msg) {
        this.messageId = msg.getId();
        this.from = msg.getFrom();
        this.to = msg.getTo();
        this.message = msg.getMessage();
        this.date = msg.getMessageDateTime();
        this.reply = msg.getReplyToMessageId();
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Set<String> getTo() {
        return to;
    }

    public String getRecipients(){
        StringBuilder rez = new StringBuilder();
        for(String user: to){
            rez.append(user).append(", ");
        }
        return rez.toString();
    }

    public void setTo(Set<String> to) {
        this.to = to;
    }

    public void addRecipient(String recipient){
        this.to.add(recipient);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date.format(Constants.DATE_TIME_FORMATTER2);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "messageId=" + messageId +
                ", from='" + from + '\'' +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", reply=" + reply +
                '}';
    }
}
