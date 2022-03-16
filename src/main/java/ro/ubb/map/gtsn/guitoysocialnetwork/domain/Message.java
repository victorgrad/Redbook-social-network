package ro.ubb.map.gtsn.guitoysocialnetwork.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Message extends Entity<Long>{
    private String from;
    private Set<String> to;
    private String message;
    private LocalDateTime messageDateTime;
    private Long replyToMessageId;

    /**
     * Class constructor
     * @param id Long, the id of the message
     * @param from String, the id of the user that sent the message
     * @param message String, the message itself
     * @param messageDateTime LocalDateTime, the date and time the message was sent
     * @param replyToMessageId Long, the id of the message which we are replying to
     */
    public Message(Long id, String from, String message, LocalDateTime messageDateTime, Long replyToMessageId) {
        this.setId(id);
        this.from = from;
        this.message = message;
        this.messageDateTime = messageDateTime;
        this.replyToMessageId = replyToMessageId;
        to = new HashSet<>();
    }

    /**
     * Class constructor
     * @param id Long, the id of the message
     * @param from String, the id of the user that sent the message
     * @param message String, the message itself
     * @param messageDateTime LocalDate, the date and time the message was sent
     */
    public Message(Long id, String from, String message, LocalDateTime messageDateTime) {
        this.setId(id);
        this.from = from;
        this.message = message;
        this.messageDateTime = messageDateTime;
        this.replyToMessageId = 0L;
        this.to = new HashSet<>();
    }

    /**
     * Returns the id of the user that sent the message
     * @return String
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the id of the user that sent the message
     * @param from String, id of the user that sent the message
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Returns a set of user id's that the message was sent to
     * @return Set<String>
     */
    public Set<String> getTo() {
        return to;
    }

    /**
     * Sets the set of user id's that the message is sent to
     * @param to Set<String>, set of user id's that the message is sent to
     */
    public void setTo(Set<String> to) {
        this.to = new HashSet<>(to);
    }

    /**
     * Returns the message itself
     * @return String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message itself
     * @param message String, the message itself
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the date and time the message was sent
     * @return LocalDateTime
     */
    public LocalDateTime getMessageDateTime() {
        return messageDateTime;
    }

    /**
     * Sets the date and time the message was sent
     * @param messageDateTime LocalDateTime, the date the message was sent
     */
    public void setMessageDateTime(LocalDateTime messageDateTime) {
        this.messageDateTime = messageDateTime;
    }

    /**
     * Returns the id of the message which the current message is replying to
     * @return Long
     */
    public Long getReplyToMessageId() {
        return replyToMessageId;
    }

    /**
     * Sets the id of the message which the current message is replying to
     * @param replyToMessageId Long, id of the message which the current message is replying to
     */
    public void setReplyToMessageId(Long replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    /**
     * Adds a recipient to the recipient set
     * @param username String, the id of the user
     * @return True - if the recipient was added successfully
     *         False - if the recipient is already part of the set
     */
    public boolean addRecipient(String username){
        return this.to.add(username);
    }

    /**
     * Removes a recipient from the recipient set
     * @param username String, the id of the user
     * @return True - if the recipient was removed successfully
     *         False - if the recipient is not part of the set
     */
    public boolean removeRecipient(String username){
        return this.to.remove(username);
    }
}
