package ro.ubb.map.gtsn.guitoysocialnetwork.domain.dtos;

import ro.ubb.map.gtsn.guitoysocialnetwork.domain.User;

import java.util.Objects;

public class UserDTO {
    private String username;
    private String firstname;
    private String lastname;
    private String passwordHash;

    /**
     * Class constructor
     * @param user User, an User object
     */
    public UserDTO(User user){
        username = user.getId();
        firstname = user.getFirstName();
        lastname = user.getLastName();
        passwordHash = user.getPasswordHash();
    }

    /**
     * Class constructor without parameters (!!!Must use set methods)
     */
    public UserDTO(){
        username = null;
        firstname = null;
        lastname = null;
        passwordHash = null;
    }

    /**
     * Returns the id of the user
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the firstname of the user
     * @return String
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Returns the lastname of the user
     * @return String
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets the username of the user
     * @param username String
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the first name of the user
     * @param firstname String
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Sets the last name of the user
     * @param lastname String
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Returns the password hash
     * @return String
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * sets the password hash
     * @param passwordHash String
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO userDTO)) return false;
        return Objects.equals(username, userDTO.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
