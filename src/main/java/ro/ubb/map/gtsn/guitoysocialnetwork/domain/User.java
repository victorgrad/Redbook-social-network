package ro.ubb.map.gtsn.guitoysocialnetwork.domain;

import java.util.*;

public class User extends Entity<String>{
    private final String firstName;
    private final String lastName;
    private String passwordHash;

    /**
     * Class constructor
     * @param id string the id of the user
     * @param firstName string the firstname of the user
     * @param lastName string the lastname of the user
     */
    public User(String id, String firstName, String lastName) {
        super.setId(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = null;
    }

    /**
     * Returns the hash of the password
     * @return string
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the hash of the password
     * @param passwordHash , String the password hash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Returns the firstname of the user
     * @return String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the lastname of the user
     * @return String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns a string with information about the user
     * @return String
     */
    @Override
    public String toString() {
        return "User{" + " username:'" + getId() + "', firstName: '" + firstName + "', lastName: '" + lastName + "' }";
    }

    /**
     * Defines the equality between two User objects
     * @param o the object to which is compared
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User that)) {
            return false;
        }
        return getId().equals(that.getId());
    }

    /**
     * Returns the hash code of the current instance
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
