package ro.ubb.map.gtsn.guitoysocialnetwork.domain;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;

import java.time.LocalDateTime;

public class Request extends Entity<Tuple<String, String>>{
    String status;
    LocalDateTime localDateTime;

    /**
     * Class constructor
     * @param from String, the username of the person that sent the request
     * @param to String, the username of the person that received the request
     * @param status String, the status of the request
     * @param localDateTime LocalDateTime, the time the request was created
     */
    public Request(String from, String to, String status, LocalDateTime localDateTime){
        this.setId(new Tuple<>(from,to));
        this.status = status;
        this.localDateTime=localDateTime;
    }

    /**
     * Class constructor that defaults the status as pending
     * @param from String, the username of the person that sent the request
     * @param to String, the username of the person that recieved the request
     * @param localDateTime LocalDateTime, the time the request was created
     */
    public Request(String from, String to, LocalDateTime localDateTime){
        this.setId(new Tuple<>(from,to));
        this.status = Constants.PENDINGREQUEST;
        this.localDateTime = localDateTime;
    }

    /**
     * Returns the username of the sender of the request
     * @return String
     */
    public String getFrom() {
        return this.getId().getFirst();
    }

    /**
     * Sets the username of the sender of the request
     * @param from String, username of user that sent the request
     */
    public void setFrom(String from) {
        this.getId().setFirst(from);
    }

    /**
     * Returns the username of the receiver of the request
     * @return String
     */
    public String getTo() {
        return this.getId().getSecond();
    }

    /**
     * Returns the date and time the request was created
     * @return LocalDateTime
     */
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    /**
     * Sets the username of the receiver of the request
     * @param to String, the username of the user that received the request
     */
    public void setTo(String to) {
        this.getId().setSecond(to);
    }

    /**
     * Returns the status of the request
     * @return String
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the request
     * @param status String, the status of the request
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the date and time of the request
     * @param localDateTime LocalDateTime
     */
    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
