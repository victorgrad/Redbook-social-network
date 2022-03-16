package ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos;

import java.time.LocalDate;

public class FriendshipDTO {
    private UserDTO user1;
    private UserDTO user2;
    private LocalDate date;

    /**
     * Class constructor
     * @param user1 UserDTO, an user
     * @param user2 UserDTO, another user
     * @param date LocalDateTime, the date of the friendship, can be null
     */
    public FriendshipDTO(UserDTO user1, UserDTO user2, LocalDate date){
        this.user1 = user1;
        this.user2 = user2;
        this.date = date;
    }

    /**
     * Class constructor without parameters (!!!Must use set methods)
     */
    public FriendshipDTO(){
        this.user1 = null;
        this.user2 = null;
        this.date = null;
    }

    /**
     * Returns the UserDTO object of the first user
     * @return UserDTO
     */
    public UserDTO getUser1() {
        return user1;
    }

    /**
     * Returns the UserDTO object of the second user
     * @return UserDTO
     */
    public UserDTO getUser2() {
        return user2;
    }

    /**
     * Reruns the date of the beginning of the friendship, can be null
     * @return LocalDateTime
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the first user of the friendship
     * @param user1 UserDTO
     */
    public void setUser1(UserDTO user1) {
        this.user1 = user1;
    }

    /**
     * Sets the second user of the friendship
     * @param user2 UserDTO
     */
    public void setUser2(UserDTO user2) {
        this.user2 = user2;
    }

    /**
     * Sets the date of the friendship
     * @param date LocalDate
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
