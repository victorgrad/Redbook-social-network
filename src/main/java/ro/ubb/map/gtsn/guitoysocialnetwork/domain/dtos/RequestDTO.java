package ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;

import java.time.LocalDateTime;

public class RequestDTO {
    UserDTO from;
    UserDTO to;
    String status;
    LocalDateTime localDateTime;

    /**
     * Class constructor
     * @param from UserDTO, the DTO of the user that sent the request
     * @param to UserDTO, the DTO of the user that received the request
     * @param status String, the status of the request
     * @param localDateTime LocalDateTime, the time the request was created
     */
    public RequestDTO(UserDTO from, UserDTO to, String status, LocalDateTime localDateTime){
        this.from = from;
        this.to = to;
        this.status = status;
        this.localDateTime = localDateTime;
    }

    /**
     * Class constructor without parameters (!!!Must use set methods)
     */
    public RequestDTO(){
        this.from = null;
        this.to = null;
        this.status = null;
        this.localDateTime = null;
    }

    /**
     * Class constructor that defaults the status as pending
     * @param from UserDTO, the DTO of the user that sent the request
     * @param to UserDTO, the DTO of the user that received the request
     */
    public RequestDTO(UserDTO from, UserDTO to){
        this.from = from;
        this.to = to;
        this.status = Constants.PENDINGREQUEST;
    }

    /**
     * Returns the DTO of the sender of the request
     * @return UserDTO
     */
    public UserDTO getFrom() {
        return from;
    }

    /**
     * Sets the DTO of the sender of the request
     * @param from UserDTO, DTO of user that sent the request
     */
    public void setFrom(UserDTO from) {
        this.from = from;
    }

    /**
     * Returns the DTO of the receiver of the request
     * @return UserDTO
     */
    public UserDTO getTo() {
        return to;
    }

    /**
     * Sets the DTO of the receiver of the request
     * @param to UserDTO, the DTO of the user that received the request
     */
    public void setTo(UserDTO to) {
        this.to = to;
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

    /**
     * Returns the date and time of the request
     * @return LocalDateTime
     */
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
