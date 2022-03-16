package ro.ubb.map.gtsn.guitoysocialnetwork.domain;

import ro.ubb.map.gtsn.guitoysocialnetwork.constants.Constants;

import java.time.LocalDate;
import java.util.Objects;

public class Friendship extends Entity<Pair<String>>{
    LocalDate friendshipDate;

    /**
     * Class constructor
     * @param id1 String, the id of the first user
     * @param id2 String, the id of the second user
     * @param friendshipDate LocalDate, the beginning date of the friendship
     */
    public Friendship(String id1, String id2, LocalDate friendshipDate){
        super.setId(new Pair<>(id1, id2));
        this.friendshipDate = friendshipDate;
    }

    /**
     * Returns the beginning date of the friendship
     * @return LocalDateTime
     */
    public LocalDate getFriendshipDate(){
        return friendshipDate;
    }

    /**
     * Returns a string with the information of the Friendship object
     * @return String
     */
    @Override
    public String toString() {
        return "Friendship{" + " userID1= " + getId().getLeft() + ", userID2= " + getId().getRight() +
                ", date: " + friendshipDate.format(Constants.DATE_TIME_FORMATTER) + " }";
    }

    /**
     * Defines the equality between two Friendship objects
     * @param o the object to which is compared the current instance
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Friendship that)) {
            return false;
        }
        return getId().equals(that.getId());
    }

    /**
     * Returns the hash code of the current instance
     * @return int, the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
